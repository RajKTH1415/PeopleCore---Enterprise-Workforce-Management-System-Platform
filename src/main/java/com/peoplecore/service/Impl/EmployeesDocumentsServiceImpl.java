package com.peoplecore.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.AuditAction;
import com.peoplecore.module.*;
import com.peoplecore.repository.*;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.service.OcrService;
import com.peoplecore.service.SkillParserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class EmployeesDocumentsServiceImpl implements EmployeesDocumentsService {


    private final ObjectMapper objectMapper;
    private final OcrService ocrService;
    private final SkillParserService skillParserService;
    private final CertificationRepository certificationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentAuditRepository documentAuditRepository;
    private final EmployeeDocumentSkillMappingRepository employeeDocumentSkillMappingRepository;
    private final EmployeeDocumentCertificationMappingRepository employeeDocumentCertificationMappingRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;


    public EmployeesDocumentsServiceImpl(ObjectMapper objectMapper, OcrService ocrService, SkillParserService skillParserService, CertificationRepository certificationRepository, EmployeeRepository employeeRepository, EmployeeCertificationsRepository employeeCertificationsRepository, EmployeeDocumentRepository employeeDocumentRepository, DocumentVersionRepository documentVersionRepository, DocumentAuditRepository documentAuditRepository, EmployeeDocumentSkillMappingRepository employeeDocumentSkillMappingRepository, EmployeeDocumentCertificationMappingRepository employeeDocumentCertificationMappingRepository) {
        this.objectMapper = objectMapper;
        this.ocrService = ocrService;
        this.skillParserService = skillParserService;
        this.certificationRepository = certificationRepository;
        this.employeeRepository = employeeRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.documentAuditRepository = documentAuditRepository;
        this.employeeDocumentSkillMappingRepository = employeeDocumentSkillMappingRepository;
        this.employeeDocumentCertificationMappingRepository = employeeDocumentCertificationMappingRepository;
    }

    /*just for testing*/
    @Override
    public void deleteAllDocumentsSystem() {

        List<EmployeeDocument> documents = employeeDocumentRepository.findAll();

        for (EmployeeDocument doc : documents) {

            //  delete file
            try {
                if (doc.getFileUrl() != null) {
                    Files.deleteIfExists(Paths.get(doc.getFileUrl()));
                }
            } catch (Exception ignored) {}

            //  delete mappings first (FK safe)
            employeeDocumentSkillMappingRepository.deleteByDocumentId(doc.getId());
            employeeDocumentCertificationMappingRepository.deleteByDocumentId(doc.getId());

            //  delete version history
            documentVersionRepository.deleteByDocumentRefId(doc.getId());

            //  delete audit logs
            documentAuditRepository.deleteByDocumentId(doc.getId());
        }

        // finally delete main table
        employeeDocumentRepository.deleteAll();
    }

    @Override
    public DocumentDetailsResponse getDocumentById(Long employeeId, String documentId) {

        // 1. Fetch document
        EmployeeDocument doc = employeeDocumentRepository
                .findByEmployeeIdAndDocumentId(employeeId, documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        //  2. Fetch version history
        List<DocumentVersionHistory> versions = documentVersionRepository
                .findByDocumentRefIdOrderByVersionDesc(doc.getId());

        List<DocumentVersionDto> versionDtos = versions.stream()
                .map(v -> DocumentVersionDto.builder()
                        .version(v.getVersion())
                        .fileName(v.getFileName())
                        .fileSize(v.getFileSize())
                        .storageKey(v.getStorageKey())
                        .uploadedBy(v.getUploadedBy())
                        .versionComment(v.getVersionComment())
                        .uploadedAt(v.getUploadedAt())
                        .build())
                .toList();

        //  3. Fetch audit logs
        List<EmployeeDocumentAudit> audits = documentAuditRepository
                .findByDocumentIdOrderByPerformedAtDesc(doc.getId());

        List<DocumentAuditDto> auditDtos = audits.stream()
                .map(a -> DocumentAuditDto.builder()
                        .action(a.getAction())
                        .actionType(a.getActionType())
                        .accessType(a.getAccessType())
                        .remarks(a.getRemarks())
                        .performedBy(a.getPerformedBy())
                        .performedAt(a.getPerformedAt())
                        .oldValue(parseJson(a.getOldValue()))
                        .newValue(parseJson(a.getNewValue()))
                        .build())
                .toList();

        // 4. Build response
        return DocumentDetailsResponse.builder()
                .documentId(doc.getDocumentId())
                .employeeId(employeeId)
                .documentType(doc.getDocumentType())
                .category(doc.getDocumentCategory())
                .title(doc.getTitle())
                .description(doc.getDescription())

                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileSize(doc.getFileSize())

                .version(doc.getVersion())
                .status(doc.getStatus())
                .verificationStatus("PENDING") // or from DB

                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())

                .isPrimary(doc.getIsPrimary())
                .tags(doc.getTags() != null ? Arrays.asList(doc.getTags()) : null)

                .uploadedAt(doc.getUploadedAt())
                .updatedAt(doc.getUpdatedAt())

                .versions(versionDtos)
                .audits(auditDtos)
                .build();
    }
    private Object parseJson(String json) {
        try {
            if (json == null) return null;
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public DocumentResponse uploadDocument(Long employeeId,
                                           MultipartFile file,
                                           String documentType,
                                           String category,
                                           String title,
                                           String description,
                                           String documentNumber,
                                           LocalDate issueDate,
                                           LocalDate expiryDate,
                                           Boolean isPrimary,
                                           List<String> tags,
                                           HttpServletRequest request) {

        try {

            // 1. FILE VALIDATION
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            long MAX_SIZE = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > MAX_SIZE) {
                throw new RuntimeException("File exceeds 5MB limit");
            }

            List<String> allowedTypes = List.of("application/pdf", "image/jpeg", "image/jpg");
            if (!allowedTypes.contains(file.getContentType())) {
                throw new RuntimeException("Invalid file type. Only PDF/JPG allowed");
            }

            //  2. READ FILE BYTES (IMPORTANT FIX)
            byte[] fileBytes = file.getBytes();

            //  3. GENERATE HASH
            String fileHash = DigestUtils.sha256Hex(fileBytes);

            // ⚡ 4. DUPLICATE CHECK
            Optional<EmployeeDocument> existingDocOpt =
                    employeeDocumentRepository.findByEmployeeIdAndFileHash(employeeId, fileHash);

            Employee employeeRef = employeeRepository.getReferenceById(employeeId);

            int version = 1;
            EmployeeDocument doc;
            String oldValueJson = null;

            if (existingDocOpt.isPresent()) {

                // UPDATE FLOW
                doc = existingDocOpt.get();

                //  capture OLD state BEFORE change
                oldValueJson = objectMapper.writeValueAsString(doc);

                version = doc.getVersion() + 1;

                doc.setVersion(version);
                doc.setUpdatedAt(LocalDateTime.now());

            } else {

                //  NEW DOCUMENT
                String documentId = "DOC_" + UUID.randomUUID().toString().substring(0, 8);

                doc = EmployeeDocument.builder()
                        .employeeId(employeeId)
                        .documentId(documentId)
                        .documentType(documentType)
                        .documentCategory(category)
                        .title(title)
                        .description(description)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .fileHash(fileHash)
                        .documentNumber(documentNumber)
                        .issueDate(issueDate)
                        .expiryDate(expiryDate)
                        .isPrimary(isPrimary)
                        .tags(tags != null ? tags.toArray(new String[0]) : null)
                        .version(1)
                        .status("ACTIVE")
                        .uploadedAt(LocalDateTime.now())
                        .createdBy("SYSTEM")
                        .build();
            }

            //  5. STORAGE STRUCTURE
            String baseDir = uploadDir + "/documents/" + employeeId + "/";
            Files.createDirectories(Paths.get(baseDir));

            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String storageFileName = doc.getDocumentId() + "_v" + version + extension;

            Path filePath = Paths.get(baseDir + storageFileName);
            Files.write(filePath, fileBytes);

            doc.setFileUrl(filePath.toString());
            doc.setFileName(file.getOriginalFilename());

            employeeDocumentRepository.save(doc);
            String newValueJson = objectMapper.writeValueAsString(doc);


            String versionComment;

            if (!existingDocOpt.isPresent()) {
                versionComment = "Initial upload";
            } else if ("RESUME".equalsIgnoreCase(documentType)) {
                versionComment = "Resume updated and reprocessed";
            } else if ("CERTIFICATE".equalsIgnoreCase(documentType)) {
                versionComment = "Certificate document updated";
            } else {
                versionComment = "File replaced with new version";
            }

            //  6. VERSION HISTORY
            DocumentVersionHistory versionHistory = DocumentVersionHistory.builder()
                    .documentRefId(doc.getId())
                    .documentId(doc.getDocumentId())
                    .version(version)
                    .fileName(doc.getFileName())
                    .fileSize(file.getSize())
                    .storageKey(storageFileName)
                    .uploadedBy("SYSTEM")
                    .versionComment(versionComment)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            documentVersionRepository.save(versionHistory);

            //  7. CONDITIONAL LOGIC
            if ("CERTIFICATE".equalsIgnoreCase(documentType)) {

                //  1. Fetch employee
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

                //  2. Fetch certification (by title)
                Certification certification = certificationRepository
                        .findByNameIgnoreCase(title)
                        .orElseGet(() -> {
                            Certification newCert = Certification.builder()
                                    .name(title)
                                    //.createdBy("SYSTEM")
                                    .build();
                            return certificationRepository.save(newCert);
                        });

                //  3. Find or create employee_certification
                EmployeeCertification employeeCertification =
                        employeeCertificationsRepository
                                .findByEmployeeAndCertification(employee, certification)
                                .orElseGet(() -> {
                                    EmployeeCertification newEmpCert = EmployeeCertification.builder()
                                            .employee(employee)
                                            .certification(certification)
                                            .issueDate(issueDate)
                                            .expiryDate(expiryDate)
                                            .status("ACTIVE")
                                            .fileUrl(doc.getFileUrl())
                                            .fileName(doc.getFileName())
                                            .fileSize(doc.getFileSize())
                                            .uploadedAt(LocalDateTime.now())
                                            .build();

                                    return employeeCertificationsRepository.save(newEmpCert);
                                });

                boolean exists = employeeDocumentCertificationMappingRepository
                        .existsByDocumentIdAndEmployeeCertificationId(
                                doc.getId(),
                                employeeCertification.getId()
                        );

                if (!employeeDocumentCertificationMappingRepository
                        .existsByDocumentIdAndEmployeeCertificationId(
                                doc.getId(),
                                employeeCertification.getId()
                        )) {

                    employeeDocumentCertificationMappingRepository.save(
                            EmployeeDocumentCertificationMapping.builder()
                                    .documentId(doc.getId())
                                    .employeeCertificationId(employeeCertification.getId())
                                    .status("ACTIVE")
                                    .createdBy("SYSTEM")
                                    .build()
                    );
                }

            } else if ("RESUME".equalsIgnoreCase(documentType)) {

                // ⚡ OCR TRIGGER
                String parsedText = ocrService.extractText(fileBytes);

                //  SKILL EXTRACTION
                List<String> skills = skillParserService.extractSkills(parsedText);

                List<EmployeeDocumentSkillMapping> mappings = skills.stream()
                        .map(skill -> EmployeeDocumentSkillMapping.builder()
                                .employeeId(employeeId)
                                .documentId(doc.getId())
                                .employeeSkillId(employeeId)
                                .build())
                        .toList();

                employeeDocumentSkillMappingRepository.saveAll(mappings);
            }

            String actionValue = existingDocOpt.isPresent()
                    ? "Document updated"
                    : "Document uploaded";

            String actionTypeValue = existingDocOpt.isPresent()
                    ? "REPLACE_FILE"
                    : "UPLOAD";

            Map<String, String> diffMap = generateOldNewDiff(oldValueJson, newValueJson);

            String oldDiffJson = diffMap.get("old");
            String newDiffJson = diffMap.get("new");

            EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                    .documentId(doc.getId())
                    .employeeId(employeeId)
                    .action(actionValue) //
                    .fileName(doc.getFileName())
                    .fileUrl(doc.getFileUrl())
                    .remarks(existingDocOpt.isPresent() ? "Version updated" : "Initial upload")
                    .actionType(actionTypeValue) //
                    .accessType("WRITE")
                    .performedBy("SYSTEM")
                    .status("SUCCESS")
                    .oldValue(oldDiffJson)
                    .newValue(newDiffJson)
                    .performedAt(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            documentAuditRepository.save(audit);

            return DocumentResponse.builder()
                    .documentId(doc.getDocumentId())
                    .employeeId(employeeId)
                    .documentType(documentType)
                    .category(category)
                    .title(title)
                    .fileUrl(doc.getFileUrl())
                    .fileName(doc.getFileName())
                    .fileSize(file.getSize())
                    .version(version)
                    .issueDate(issueDate)
                    .isPrimary(true)
                    .expiryDate(doc.getExpiryDate())
                    .tags(tags)
                    .status("ACTIVE")
                    .verificationStatus("PENDING")
                    .uploadedAt(doc.getUploadedAt())
                    .updatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

    }

    @Override
    public PageResponse<DocumentResponse> getAllDocuments(
            Long employeeId,
            String documentType,
            String category,
            String verificationStatus,
            Boolean isDeleted,
            Boolean isPrimary,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            Boolean expired,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<EmployeeDocument> spec = buildSpecification(
                employeeId,
                documentType,
                category,
                verificationStatus,
                isDeleted,
                isPrimary,
                expiryBefore,
                expiryAfter,
                expired,
                search,
                tags
        );

        Page<EmployeeDocument> result = employeeDocumentRepository.findAll(spec, pageable);

        List<DocumentResponse> content = result.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<DocumentResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .first(result.isFirst())
                .last(result.isLast())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .sortBy(sortBy)
                .direction(sortDir)
                .build();
    }

    @Override
    public PageResponse<DocumentResponse> getDocuments(
            Long employeeId,
            String type,
            String status,
            String verificationStatus,
            String category,
            Boolean isDeleted,
            Boolean isPrimary,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        // ✅ SAFE SORT
        Set<String> allowedSortFields = Set.of(
                "uploadedAt", "updatedAt", "fileName", "documentType"
        );

        sortBy = (sortBy == null || sortBy.isBlank()) ? "uploadedAt" : sortBy.trim();

        if (sortBy.contains(",")) {
            sortBy = sortBy.split(",")[0];
        }

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "uploadedAt";
        }

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<EmployeeDocument> spec = buildSpecification(
                employeeId,
                type,
                status,
                verificationStatus,
                category,
                isDeleted,
                isPrimary,
                search,
                tags
        );

        Page<EmployeeDocument> result = employeeDocumentRepository.findAll(spec, pageable);

        return PageResponse.<DocumentResponse>builder()
                .content(result.getContent().stream().map(this::mapToResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .first(result.isFirst())
                .last(result.isLast())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .sortBy(sortBy)
                .direction(sortDir)
                .build();
    }

    @Override
    public DocumentResponse getDocumentById(String documentId) {

        EmployeeDocument document = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));

        return mapToResponse(document);
    }

    private Specification<EmployeeDocument> buildSpecification(
            Long employeeId,
            String type,
            String status,
            String verificationStatus,
            String category,
            Boolean isDeleted,
            Boolean isPrimary,
            String search,
            List<String> tags
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Employee filter
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employeeId"), employeeId));
            }

            // Type
            if (type != null) {
                predicates.add(cb.equal(root.get("documentType"), type));
            }

            // Category
            if (category != null) {
                predicates.add(cb.equal(root.get("documentCategory"), category));
            }

            // Status
            if (status != null) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
            }

            // Verification status
            if (verificationStatus != null) {
                predicates.add(cb.equal(root.get("verificationStatus"), verificationStatus));
            }

            // Primary
            if (Boolean.TRUE.equals(isPrimary)) {
                predicates.add(cb.isTrue(root.get("isPrimary")));
            }

            // Deleted
            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("isDeleted"), isDeleted));
            }

            // Search
            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("fileName")), like)
                ));
            }

            // Tags
            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tag : tags) {
                    tagPredicates.add(cb.like(
                            cb.function("array_to_string", String.class,
                                    root.get("tags"),
                                    cb.literal(",")
                            ),
                            "%" + tag.toLowerCase() + "%"
                    ));
                }

                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<EmployeeDocument> buildSpecification(
            Long employeeId,
            String documentType,
            String category,
            String verificationStatus,
            Boolean isDeleted,
            Boolean isPrimary,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            Boolean expired,
            String search,
            List<String> tags
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ✅ FIXED: correct field usage
            predicates.add(cb.equal(root.get("employeeId"), employeeId));

            predicates.add(cb.or(
                    cb.isNull(root.get("status")),
                    cb.equal(cb.lower(cb.trim(root.get("status"))), "active")
            ));

            if (documentType != null)
                predicates.add(cb.equal(root.get("documentType"), documentType));

            if (category != null)
                predicates.add(cb.equal(root.get("documentCategory"), category));

            if (verificationStatus != null)
                predicates.add(cb.equal(root.get("verificationStatus"), verificationStatus));

            if (Boolean.TRUE.equals(isPrimary))
                predicates.add(cb.isTrue(root.get("isPrimary")));

            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("isDeleted"), isDeleted));
            }

            if (expiryBefore != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("expiryDate"), expiryBefore));

            if (expiryAfter != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), expiryAfter));

            if (Boolean.TRUE.equals(expired))
                predicates.add(cb.lessThan(root.get("expiryDate"), LocalDate.now()));

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("fileName")), like)
                ));
            }

            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tag : tags) {
                    tagPredicates.add(cb.like(
                            cb.function("array_to_string", String.class,
                                    root.get("tags"),
                                    cb.literal(",")
                            ),
                            "%" + tag.toLowerCase() + "%"
                    ));
                }

                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private DocumentResponse mapToResponse(EmployeeDocument doc) {
        return DocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .employeeId(doc.getEmployeeId())
                .documentType(doc.getDocumentType())
                .category(doc.getDocumentCategory())
                .title(doc.getTitle())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileSize(doc.getFileSize())
                .version(doc.getVersion())
                .status(doc.getStatus())
                .verificationStatus(doc.getVerificationStatus())
                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())
                .isPrimary(doc.getIsPrimary())
                .tags(doc.getTags() != null ? Arrays.asList(doc.getTags()) : null)
                .uploadedAt(doc.getUploadedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }


    private Map<String, String> generateOldNewDiff(String oldJson, String newJson) {
        try {
            if (oldJson == null || newJson == null) return Map.of();

            Map<String, Object> oldMap = objectMapper.readValue(oldJson, Map.class);
            Map<String, Object> newMap = objectMapper.readValue(newJson, Map.class);

            Map<String, Object> oldDiff = new HashMap<>();
            Map<String, Object> newDiff = new HashMap<>();

            for (String key : newMap.keySet()) {
                Object oldVal = oldMap.get(key);
                Object newVal = newMap.get(key);

                if (!Objects.equals(oldVal, newVal)) {
                    oldDiff.put(key, oldVal);
                    newDiff.put(key, newVal);
                }
            }

            return Map.of(
                    "old", objectMapper.writeValueAsString(oldDiff),
                    "new", objectMapper.writeValueAsString(newDiff)
            );

        } catch (Exception e) {
            throw new RuntimeException("Diff generation failed", e);
        }
    }
}
