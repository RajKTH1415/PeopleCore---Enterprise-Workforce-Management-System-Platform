package com.peoplecore.service.Impl;

import com.peoplecore.constants.CertificationAuditActions;
import com.peoplecore.dto.response.DownloadCertificateResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PreviewCertificateResponse;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.EmployeeCertification;
import com.peoplecore.module.EmployeeCertificationAudit;
import com.peoplecore.repository.EmployeeCertificationAuditRepository;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import com.peoplecore.service.EmployeeCertificationFileService;
import com.peoplecore.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeCertificationFileServiceImpl implements EmployeeCertificationFileService {


    private final EmployeeCertificationAuditRepository employeeCertificationAuditRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;
    private final FileStorageService fileStorageService;


    public EmployeeCertificationFileServiceImpl(EmployeeCertificationAuditRepository employeeCertificationAuditRepository, EmployeeCertificationsRepository employeeCertificationsRepository, FileStorageService fileStorageService) {
        this.employeeCertificationAuditRepository = employeeCertificationAuditRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
        this.fileStorageService = fileStorageService;
    }

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Maximum file size is 5 MB");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only PDF, PNG and JPEG are allowed");
        }
    }

    @Override
    @Transactional
    public EmployeeCertificationResponse uploadCertificate(
            Long employeeId,
            Long certificationId,
            MultipartFile file) {

        validateFile(file);

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId, certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee Certification not found"));

        try {

            String folder = "certificates/employee-" + employeeId;
            String fileUrl = fileStorageService.uploadFile(file, folder);

            certification.setFileUrl(fileUrl);
            certification.setFileName(file.getOriginalFilename());
            certification.setFileType(file.getContentType());
            certification.setFileSize(file.getSize());
            certification.setUploadedAt(LocalDateTime.now());

            EmployeeCertification saved =
                    employeeCertificationsRepository.save(certification);

            EmployeeCertificationAudit audit = EmployeeCertificationAudit.builder()
                    .employeeId(employeeId)
                    .certificationId(certificationId)
                    .action(CertificationAuditActions.FILE_UPLOADED)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileUrl(fileUrl)
                    .performedBy("SYSTEM")
                    .performedAt(LocalDateTime.now())
                    .remarks("Certificate uploaded successfully")
                    .build();

            employeeCertificationAuditRepository.save(audit);

            return EmployeeCertificationResponse.builder()
                    .id(saved.getId())
                    .employeeId(saved.getEmployee().getId())
                    .employeeName(saved.getEmployee().getFirstName() + " "
                            + saved.getEmployee().getLastName())
                    .certificationName(saved.getCertification().getName())
                    .certificationNumber(saved.getCertificateNumber())
                    .issueDate(saved.getIssueDate())
                    .expiryDate(saved.getExpiryDate())
                    .status(saved.getStatus())
                    .proofUrl(saved.getProofUrl())
                    .fileName(saved.getFileName())
                    .fileType(saved.getFileType())
                    .fileUrl(buildFileAccessUrl(employeeId, certificationId))
                    .storagePath(saved.getFileUrl())
                    .fileSize(saved.getFileSize())
                    .certificateUploaded(saved.getFileUrl() != null)
                    .uploadedAt(saved.getUploadedAt())
                    .verifiedBy(saved.getVerifiedBy())
                    .verifiedDate(saved.getVerifiedDate())
                    .verificationNotes(saved.getVerificationNotes())
                    .isDeleted(saved.getIsDeleted())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while uploading certificate: " + e.getMessage(), e);
        }
    }

    private String buildFileAccessUrl(Long employeeId, Long certificationId) {
        return String.format(
                "/api/v1/employee/file/%d/certifications/%d/download",
                employeeId,
                certificationId
        );
    }


    @Override
    @Transactional(readOnly = true)
    public DownloadCertificateResponse downloadCertificate(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification =
                getEmployeeCertification(employeeId, certificationId);

        if (certification.getFileUrl() == null) {
            throw new ResourceNotFoundException("Certificate file not found");
        }

        byte[] data =
                fileStorageService.downloadFile(certification.getFileUrl());

        return DownloadCertificateResponse.builder()
                .fileName(certification.getFileName())
                .contentType(certification.getFileType())
                .fileData(data)
                .fileSize(certification.getFileSize())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public PreviewCertificateResponse previewCertificate(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId, certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee Certification not found"));

        if (certification.getFileUrl() == null
                || certification.getFileUrl().isBlank()) {
            throw new ResourceNotFoundException(
                    "No certificate file available for preview");
        }

        try {
            byte[] fileData =
                    fileStorageService.downloadFile(
                            certification.getFileUrl());

            return PreviewCertificateResponse.builder()
                    .message("Certificate preview loaded successfully")
                    .fileName(certification.getFileName())
                    .contentType(
                            certification.getFileType() != null
                                    ? certification.getFileType()
                                    : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .fileData(fileData)
                    .fileSize(certification.getFileSize())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to preview certificate: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteCertificateFile(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId, certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee Certification not found"));

        if (certification.getFileUrl() == null
                || certification.getFileUrl().isBlank()) {
            throw new ResourceNotFoundException(
                    "No uploaded certificate file found");
        }

        String deletedFileName = certification.getFileName();
        String deletedFileUrl = certification.getFileUrl();

        try {
            // Delete from cloud storage
            fileStorageService.deleteFile(deletedFileUrl);

            // Remove file metadata from database
            certification.setFileUrl(null);
            certification.setFileName(null);
            certification.setFileType(null);
            certification.setFileSize(null);
            certification.setUploadedAt(null);

            employeeCertificationsRepository.save(certification);

            // Save audit log
            EmployeeCertificationAudit audit =
                    EmployeeCertificationAudit.builder()
                            .employeeId(employeeId)
                            .certificationId(certificationId)
                            .action(CertificationAuditActions.FILE_DELETED)
                            .fileName(deletedFileName)
                            .fileUrl(deletedFileUrl)
                            .performedBy("SYSTEM")
                            .performedAt(LocalDateTime.now())
                            .remarks("Certificate file deleted successfully")
                            .build();

            employeeCertificationAuditRepository.save(audit);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to delete certificate file: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public EmployeeCertificationResponse replaceCertificateFile(
            Long employeeId,
            Long certificationId,
            MultipartFile file) {

        validateFile(file);

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId, certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee Certification not found"));

        String oldFileName = certification.getFileName();
        String oldFileUrl = certification.getFileUrl();

        try {
            // Delete existing file from cloud storage
            if (oldFileUrl != null && !oldFileUrl.isBlank()) {
                fileStorageService.deleteFile(oldFileUrl);
            }

            // Upload new file
            String folder = "certificates/employee-" + employeeId;
            String newFileUrl =
                    fileStorageService.uploadFile(file, folder);

            // Update certification
            certification.setFileUrl(newFileUrl);
            certification.setFileName(file.getOriginalFilename());
            certification.setFileType(file.getContentType());
            certification.setFileSize(file.getSize());
            certification.setUploadedAt(LocalDateTime.now());

            EmployeeCertification saved =
                    employeeCertificationsRepository.save(certification);

            // Audit log
            EmployeeCertificationAudit audit =
                    EmployeeCertificationAudit.builder()
                            .employeeId(employeeId)
                            .certificationId(certificationId)
                            .action(CertificationAuditActions.FILE_REPLACED)
                            .fileName(file.getOriginalFilename())
                            .fileType(file.getContentType())
                            .fileUrl(newFileUrl)
                            .performedBy("SYSTEM")
                            .performedAt(LocalDateTime.now())
                            .remarks(
                                    oldFileName == null || oldFileName.isBlank()
                                            ? "Certificate uploaded for the first time via replace API"
                                            : "Certificate replaced. Old file: " + oldFileName
                            )
                            .build();

            employeeCertificationAuditRepository.save(audit);

            // Response
            return EmployeeCertificationResponse.builder()
                    .id(saved.getId())
                    .employeeId(saved.getEmployee().getId())
                    .employeeName(saved.getEmployee().getFirstName() + " "
                            + saved.getEmployee().getLastName())
                    .certificationName(saved.getCertification().getName())
                    .certificationNumber(saved.getCertificateNumber())
                    .issueDate(saved.getIssueDate())
                    .expiryDate(saved.getExpiryDate())
                    .status(saved.getStatus())
                    .proofUrl(saved.getProofUrl())
                    .fileName(saved.getFileName())
                    .fileType(saved.getFileType())
                    .fileSize(saved.getFileSize())
                    .certificateUploaded(saved.getFileUrl() != null)
                    .uploadedAt(saved.getUploadedAt())
                    .verifiedBy(saved.getVerifiedBy())
                    .verifiedDate(saved.getVerifiedDate())
                    .verificationNotes(saved.getVerificationNotes())
                    .isDeleted(saved.getIsDeleted())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to replace certificate file: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateDownloadUrl(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification =
                getEmployeeCertification(employeeId, certificationId);

        if (certification.getFileUrl() == null) {
            throw new ResourceNotFoundException("Certificate file not found");
        }

        return fileStorageService.generatePresignedUrl(
                certification.getFileUrl());
    }

    private EmployeeCertification getEmployeeCertification(
            Long employeeId,
            Long certificationId) {

        return employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                        employeeId,
                        certificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee Certification not found with Employee ID: "
                                + employeeId
                                + " and Certification ID: "
                                + certificationId));
    }
}
