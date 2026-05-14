package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.BulkUpdateCertificationRequest;
import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.request.CertificationSkillRequest;
import com.peoplecore.dto.request.UpdateCertificationStatusRequest;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.CertificationStatus;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.Certification;
import com.peoplecore.module.Skill;
import com.peoplecore.repository.CertificationRepository;
import com.peoplecore.repository.EmployeeCertificationAuditRepository;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import com.peoplecore.repository.SkillRepository;
import com.peoplecore.service.CertificationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;
    private final SkillRepository skillRepository;
    private final EmployeeCertificationAuditRepository employeeCertificationAuditRepository;

    public CertificationServiceImpl(CertificationRepository certificationRepository, EmployeeCertificationsRepository employeeCertificationsRepository, SkillRepository skillRepository, EmployeeCertificationAuditRepository employeeCertificationAuditRepository) {
        this.certificationRepository = certificationRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
        this.skillRepository = skillRepository;
        this.employeeCertificationAuditRepository = employeeCertificationAuditRepository;
    }

    @Override
    @Transactional
    public List<CertificationResponse> bulkCreateCertifications(
            List<CertificationRequest> requests) {

        List<Certification> certifications = requests.stream()
                .map(request -> {

                    boolean exists = certificationRepository
                            .existsByNameIgnoreCaseAndIssuerIgnoreCase(
                                    request.getName(),
                                    request.getIssuer()
                            );

                    if (exists) {
                        throw new BadRequestException(
                                "Certification already exists: "
                                        + request.getName()
                        );
                    }

                    return Certification.builder()
                            .name(request.getName())
                            .issuer(request.getIssuer())
                            .status(CertificationStatus.ACTIVE)
                            .isDeleted(false)
                            .build();
                })
                .toList();

        List<Certification> savedCertifications =
                certificationRepository.saveAll(certifications);

        return savedCertifications.stream()
                .map(certification -> CertificationResponse.builder()
                        .id(certification.getId())
                        .name(certification.getName())
                        .issuer(certification.getIssuer())
                        .status(certification.getStatus())
                        .isDeleted(certification.isDeleted())
                        .createdDate(certification.getCreatedDate())
                        .createdBy(certification.getCreatedBy())
                        .build())
                .toList();
    }

    @Override
    public List<CertificationAuditResponse>
    getCertificationAudit(Long certificationId) {

        certificationRepository.findById(certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Certification not found"
                        ));

        return employeeCertificationAuditRepository
                .findByCertificationIdOrderByPerformedAtDesc(certificationId)
                .stream()
                .map(audit -> CertificationAuditResponse.builder()
                        .id(audit.getId())
                        .employeeId(audit.getEmployeeId())
                        .certificationId(audit.getCertificationId())
                        .action(audit.getAction())
                        .fileName(audit.getFileName())
                        .fileType(audit.getFileType())
                        .performedBy(audit.getPerformedBy())
                        .performedAt(audit.getPerformedAt())
                        .remarks(audit.getRemarks())
                        .fileUrl(audit.getFileUrl())
                        .build())
                .toList();
    }


    @Override
    public List<String> getSuggestions(String query) {

        if (query == null || query.trim().length() < 2) {
            throw new BadRequestException("Minimum 2 characters required");
        }

        return certificationRepository.searchSuggestions(query.trim());
    }

    @Override
    public CertificationResponse createCertification(CertificationRequest request) {

        certificationRepository.findByNameAndIssuerAndIsDeletedFalse(request.getName(),request.getIssuer()).ifPresent(cert->{
            throw new RuntimeException("Certification already exists");
        });

        Certification certification = new Certification();
        certification.setName(request.getName());
        certification.setIssuer(request.getIssuer());

        Certification savedCertification = certificationRepository.save(certification);
        return CertificationResponse.builder()
                .id(savedCertification.getId())
                .name(savedCertification.getName())
                .issuer(savedCertification.getIssuer())
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Override
    public CertificationResponse getById(Long id) {
     Certification certification =   certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not found with ID :"+ id));

        if (Boolean.TRUE.equals(certification.isDeleted())) {
            throw new RuntimeException("Certificate not found with ID: " + id);
        }
        return CertificationResponse.builder()
                .name(certification.getName())
                .issuer(certification.getIssuer())
                .deleted(certification.isDeleted())
                .createdBy(certification.getCreatedBy())
                .createdDate(certification.getCreatedDate())
                .updatedDate(certification.getUpdatedDate())
                .updatedBy(certification.getUpdatedBy())
                .build();
    }

    @Override
    public CertificationResponse deleteCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not  found with ID :"+ id));

        if (Boolean.TRUE.equals(certification.isDeleted())){
            throw new RuntimeException("certificate already deleted with ID:"+ id);
        }
        certification.setDeleted(true);

        certification.setCreatedDate(LocalDateTime.now());
        certification.setCreatedBy("SYSTEM");
        certification.setUpdatedBy("SYSTEM");
        certification.setUpdatedDate(LocalDateTime.now());
        certification.setDeletedAt(LocalDateTime.now());
        certification.setDeletedBy("SYSTEM");

        Certification savedCertificate = certificationRepository.save(certification);

        return CertificationResponse.builder()
                .name(savedCertificate.getName())
                .issuer(savedCertificate.getIssuer())
                .deleted(savedCertificate.isDeleted())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CertificationResponse> getAllCertifications(
            int page,
            int size,
            String sortBy,
            String direction,
            String name,
            String issuer,
            String search,
            Boolean includeDeleted //  NEW
    ) {

        // Handle null / blank inputs (VERY IMPORTANT)
        name = (name != null && !name.trim().isEmpty()) ? name : null;
        issuer = (issuer != null && !issuer.trim().isEmpty()) ? issuer : null;
        search = (search != null && !search.trim().isEmpty()) ? search : null;

        // Sorting
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // fallback if invalid field
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdDate";
        }

        Sort sort = Sort.by(sortDirection, sortBy)
                .and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Certification> certificationPage =
                certificationRepository.findCertificationsWithFilters(
                        includeDeleted,
                        name,
                        issuer,
                        search,
                        pageable
                );

        List<CertificationResponse> responses = certificationPage.getContent()
                .stream()
                .map(cert -> CertificationResponse.builder()
                        .id(cert.getId())
                        .name(cert.getName())
                        .issuer(cert.getIssuer())
                        .deleted(cert.isDeleted())
                        .createdDate(cert.getCreatedDate())
                        .createdBy(cert.getCreatedBy())
                        .updatedDate(cert.getUpdatedDate())
                        .updatedBy(cert.getUpdatedBy())
                        .build()
                )
                .toList();

        // Page Response
        return PageResponse.<CertificationResponse>builder()
                .content(responses)
                .page(certificationPage.getNumber())
                .size(certificationPage.getSize())
                .totalElements(certificationPage.getTotalElements())
                .totalPages(certificationPage.getTotalPages())
                .numberOfElements(certificationPage.getNumberOfElements())
                .first(certificationPage.isFirst())
                .last(certificationPage.isLast())
                .hasNext(certificationPage.hasNext())
                .hasPrevious(certificationPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public CertificationResponse updateCertification(Long id, CertificationRequest request) {
      Certification certification =   certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not found with ID :"+ id));

      if (Boolean.TRUE.equals(certification.isDeleted())){
          throw new RuntimeException("Cannot update a deleted certification");
      }

        String name = request.getName().trim();
        String issuer = request.getIssuer().trim();

       boolean exists =  certificationRepository.existsByNameIgnoreCaseAndIdNot(name,id);
       if (exists){
           throw new RuntimeException("Certificate already exists");
       }
       certification.setName(name);
       certification.setIssuer(issuer);

       certification.setUpdatedDate(LocalDateTime.now());
       certification.setUpdatedBy("SYSTEM");

       certification.setCreatedBy("SYSTEM");
       certification.setCreatedDate(LocalDateTime.now());

        Certification savedCertificate =  certificationRepository.save(certification);

        return CertificationResponse.builder()
                .name(savedCertificate.getName())
                .issuer(savedCertificate.getIssuer())
                .deleted(savedCertificate.isDeleted())
                .createdBy(savedCertificate.getCreatedBy())
                .createdDate(savedCertificate.getCreatedDate())
                .updatedBy(savedCertificate.getUpdatedBy())
                .updatedDate(savedCertificate.getUpdatedDate())
                .build();

    }

    @Override
    @Transactional
    public List<CertificationResponse> bulkUpdateCertifications(
            List<BulkUpdateCertificationRequest> requests) {

        List<CertificationResponse> responses = new ArrayList<>();

        for (BulkUpdateCertificationRequest request : requests) {

            Certification certification = certificationRepository
                    .findCertificationIncludingDeleted(request.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Certification not found with id: "
                                            + request.getId()
                            ));

            certification.setName(request.getName());
            certification.setIssuer(request.getIssuer());

            if (request.getStatus() != null) {
                certification.setStatus(request.getStatus());
            }

            Certification savedCertification =
                    certificationRepository.save(certification);

            responses.add(
                    CertificationResponse.builder()
                            .id(savedCertification.getId())
                            .name(savedCertification.getName())
                            .issuer(savedCertification.getIssuer())
                            .status(savedCertification.getStatus())
                            .isDeleted(savedCertification.isDeleted())
                            .createdDate(savedCertification.getCreatedDate())
                            .createdBy(savedCertification.getCreatedBy())
                            .updatedDate(savedCertification.getUpdatedDate())
                            .updatedBy(savedCertification.getUpdatedBy())
                            .build()
            );
        }

        return responses;
    }
    @Override
    @Transactional
    public CertificationResponse restoreCertification(Long id) {

        Certification certification = certificationRepository
                .findCertificationIncludingDeleted(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Certification not found with id: " + id
                        ));

        if (!certification.isDeleted()) {
            throw new RuntimeException(
                    "Certification is not deleted"
            );
        }

        certification.setDeleted(false);
        certification.setDeletedAt(null);
        certification.setDeletedBy(null);

        Certification savedCertification =
                certificationRepository.save(certification);

        return CertificationResponse.builder()
                .id(savedCertification.getId())
                .name(savedCertification.getName())
                .issuer(savedCertification.getIssuer())
                .isDeleted(savedCertification.isDeleted())
                .createdDate(savedCertification.getCreatedDate())
                .createdBy(savedCertification.getCreatedBy())
                .updatedDate(savedCertification.getUpdatedDate())
                .updatedBy(savedCertification.getUpdatedBy())
                .build();
    }

    @Override
    @Transactional
    public void permanentlyDeleteCertification(Long id) {

        Certification certification = certificationRepository
                .findCertificationIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Certification not found with id: " + id
                        ));
        if (!certification.isDeleted()) {
            throw new BadRequestException(
                    "Only soft deleted certifications can be permanently deleted"
            );
        }
        employeeCertificationsRepository
                .deleteByCertificationId(id);

        certificationRepository.delete(certification);
    }

    @Override
    @Transactional
    public CertificationResponse updateCertificationStatus(
            Long id,
            UpdateCertificationStatusRequest request
    ) {

        Certification certification = certificationRepository
                .findCertificationIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Certification not found with id: " + id
                        ));

        certification.setStatus(request.getStatus());

        Certification savedCertification =
                certificationRepository.save(certification);

        return CertificationResponse.builder()
                .id(savedCertification.getId())
                .name(savedCertification.getName())
                .issuer(savedCertification.getIssuer())
                .status(savedCertification.getStatus())
                .isDeleted(savedCertification.isDeleted())
                .createdDate(savedCertification.getCreatedDate())
                .createdBy(savedCertification.getCreatedBy())
                .updatedDate(savedCertification.getUpdatedDate())
                .updatedBy(savedCertification.getUpdatedBy())
                .build();
    }

    @Override
    public CertificationUsageAnalyticsResponse getCertificationUsageAnalytics() {

        long total = certificationRepository.count();

        List<Object[]> statusCounts = certificationRepository.countByStatus();

        long active = 0, inactive = 0, deprecated = 0;

        for (Object[] row : statusCounts) {
            String status = row[0].toString();
            Long count = (Long) row[1];

            switch (status) {
                case "ACTIVE" -> active = count;
                case "INACTIVE" -> inactive = count;
                case "DEPRECATED" -> deprecated = count;
            }
        }

        List<Object[]> issuers = certificationRepository.findTopIssuers();
        String topIssuer = issuers.isEmpty()
                ? null
                : issuers.get(0)[0].toString();

        List<Object[]> mostAssigned =
                employeeCertificationsRepository.findMostAssignedCertification();

        String topCertification = mostAssigned.isEmpty()
                ? null
                : mostAssigned.get(0)[0].toString();

        return CertificationUsageAnalyticsResponse.builder()
                .totalCertifications(total)
                .activeCount(active)
                .inactiveCount(inactive)
                .deprecatedCount(deprecated)
                .mostPopularIssuer(topIssuer)
                .mostAssignedCertification(topCertification)
                .build();
    }

    @Override
    @Transactional
    public CertificationSkillResponse addSkills(Long id, CertificationSkillRequest request) {

        Certification certification = certificationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Certification not found: " + id)
                );

        Set<Skill> skills = request.getSkillIds().stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Skill not found: " + skillId)))
                .collect(Collectors.toSet());

        certification.getSkills().addAll(skills);

        Certification saved = certificationRepository.save(certification);

        return CertificationSkillResponse.builder()
                .certificationId(saved.getId())
                .certificationName(saved.getName())
                .skills(saved.getSkills().stream()
                        .map(Skill::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public CertificationSkillResponse getSkills(Long id) {

        Certification certification = certificationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Certification not found: " + id)
                );

        return CertificationSkillResponse.builder()
                .certificationId(certification.getId())
                .certificationName(certification.getName())
                .skills(certification.getSkills().stream()
                        .map(Skill::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public String exportCertifications(
            String format,
            CertificationStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {

        List<Certification> certifications;

        if (status != null && from != null && to != null) {

            certifications =
                    certificationRepository
                            .findByStatusAndCreatedDateBetween(
                                    status,
                                    from,
                                    to
                            );

        } else if (status != null) {

            certifications =
                    certificationRepository
                            .findByStatus(status);

        } else if (from != null && to != null) {

            certifications =
                    certificationRepository
                            .findByCreatedDateBetween(
                                    from,
                                    to
                            );

        } else {

            certifications =
                    certificationRepository.findAll();
        }

        try {

            byte[] data;

            if ("csv".equalsIgnoreCase(format)) {

                data = exportCsv(certifications);

            } else if ("excel".equalsIgnoreCase(format)) {

                data = exportExcel(certifications);

            } else {

                throw new BadRequestException(
                        "Unsupported export format"
                );
            }

            Path exportDirectory =
                    Paths.get("exports");

            if (!Files.exists(exportDirectory)) {

                Files.createDirectories(exportDirectory);
            }

            String fileName =
                    "certifications_" +
                            System.currentTimeMillis() +
                            (
                                    "excel".equalsIgnoreCase(format)
                                            ? ".xlsx"
                                            : ".csv"
                            );

            Path filePath =
                    exportDirectory.resolve(fileName);

            Files.write(filePath, data);

            return fileName;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export certifications",
                    e
            );
        }
    }

    @Override
    public List<ExportHistoryResponse> getExportHistory() {

        try {

            Path exportDirectory =
                    Paths.get("exports");

            if (!Files.exists(exportDirectory)) {

                return Collections.emptyList();
            }

            List<ExportHistoryResponse> exportHistory =
                    Files.list(exportDirectory)

                            .filter(Files::isRegularFile)

                            .map(file -> {

                                try {

                                    String fileName =
                                            file.getFileName().toString();

                                    String format =
                                            fileName.endsWith(".xlsx")
                                                    ? "excel"
                                                    : "csv";

                                    return ExportHistoryResponse.builder()
                                            .fileName(fileName)
                                            .format(format)
                                            .size(
                                                    Files.size(file)
                                            )
                                            .createdAt(
                                                    Files.getLastModifiedTime(file)
                                                            .toString()
                                            )
                                            .downloadUrl(
                                                    "/api/v1/certifications/download/"
                                                            + fileName
                                            )
                                            .build();

                                } catch (Exception e) {

                                    throw new RuntimeException(e);
                                }
                            })

                            .sorted(
                                    Comparator.comparing(
                                            ExportHistoryResponse::getCreatedAt
                                    ).reversed()
                            )

                            .toList();

            return exportHistory;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to fetch export history",
                    e
            );
        }
    }

    private byte[] exportCsv(
            List<Certification> certifications
    ) throws IOException {

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        PrintWriter writer =
                new PrintWriter(outputStream);

        writer.println(
                "ID,Name,Issuer,Status"
        );

        for (Certification certification : certifications) {

            writer.println(
                    certification.getId() + "," +
                            certification.getName() + "," +
                            certification.getIssuer() + "," +
                            certification.getStatus()
            );
        }

        writer.flush();

        return outputStream.toByteArray();
    }
    private byte[] exportExcel(
            List<Certification> certifications
    ) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Certifications");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Issuer");
        header.createCell(3).setCellValue("Status");

        int rowNum = 1;

        for (Certification certification : certifications) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(certification.getId());

            row.createCell(1)
                    .setCellValue(certification.getName());

            row.createCell(2)
                    .setCellValue(
                            certification.getIssuer()
                    );

            row.createCell(3)
                    .setCellValue(
                            certification.getStatus() != null
                                    ? certification.getStatus().name()
                                    : ""
                    );
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        workbook.write(outputStream);

        workbook.close();

        return outputStream.toByteArray();
    }
}
