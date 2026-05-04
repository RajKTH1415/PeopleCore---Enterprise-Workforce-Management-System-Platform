package com.peoplecore.service.Impl;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.module.DocumentVersionHistory;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.module.EmployeeDocumentAudit;
import com.peoplecore.repository.DocumentAuditRepository;
import com.peoplecore.repository.DocumentVersionRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.EmployeesDocumentsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeesDocumentsServiceImpl implements EmployeesDocumentsService {


    private final EmployeeDocumentRepository employeeDocumentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentAuditRepository documentAuditRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    public EmployeesDocumentsServiceImpl(EmployeeDocumentRepository employeeDocumentRepository, DocumentVersionRepository documentVersionRepository, DocumentAuditRepository documentAuditRepository) {
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.documentAuditRepository = documentAuditRepository;
    }


    @Override
    public DocumentResponse uploadDocument(Long employeeId, MultipartFile file, String documentType, String category, String title, String description, String documentNumber, LocalDate issueDate, LocalDate expiryDate, Boolean isPrimary, List<String> tags, HttpServletRequest request) {
        try {
            //  1. Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            //  2. Generate document ID
            String documentId = "DOC_" + UUID.randomUUID().toString().substring(0, 8);

            //  3. Save file locally
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String storageFileName = documentId + extension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(storageFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Generate file metadata
            String fileUrl = filePath.toString();
            String fileHash = DigestUtils.sha256Hex(file.getInputStream());

            //  5. Save into employee_documents
            EmployeeDocument doc = EmployeeDocument.builder()
                    .employeeId(employeeId)
                    .documentId(documentId)
                    .documentType(documentType)
                    .documentCategory(category)
                    .title(title)
                    .description(description)
                    .fileName(originalFileName)
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileHash(fileHash)
                    .documentNumber(documentNumber)
                    .issueDate(issueDate)
                    .expiryDate(expiryDate)
                    .isPrimary(isPrimary)
                    .tags(tags != null ? tags.toArray(new String[0]) : null)
                    .uploadedAt(LocalDateTime.now())
                    .createdBy("SYSTEM")
                    .build();

            employeeDocumentRepository.save(doc);

            //  6. Save version history
            DocumentVersionHistory version = DocumentVersionHistory.builder()
                    .documentRefId(doc.getId())
                    .documentId(documentId)
                    .version(1)
                    .fileName(originalFileName)
                    .fileSize(file.getSize())
                    .storageKey(storageFileName)
                    .uploadedBy("SYSTEM")
                    .build();

            documentVersionRepository.save(version);


            EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                    .documentId(doc.getId())
                    .employeeId(employeeId)
                    .action("Document uploaded")
                    .actionType("UPLOAD")
                    .accessType("WRITE")
                    .performedBy("SYSTEM")
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            documentAuditRepository.save(audit);

            return DocumentResponse.builder()
                    .documentId(documentId)
                    .employeeId(employeeId)
                    .documentType(documentType)
                    .category(category)
                    .title(title)
                    .fileUrl(fileUrl)
                    .fileName(originalFileName)
                    .fileSize(file.getSize())
                    .version(1)
                    .status("ACTIVE")
                    .verificationStatus("PENDING")
                    .uploadedAt(doc.getUploadedAt())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
