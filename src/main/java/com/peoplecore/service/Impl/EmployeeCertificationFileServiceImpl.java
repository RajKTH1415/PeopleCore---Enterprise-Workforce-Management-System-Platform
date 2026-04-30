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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class EmployeeCertificationFileServiceImpl implements EmployeeCertificationFileService {


    private final EmployeeCertificationAuditRepository employeeCertificationAuditRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;

    public EmployeeCertificationFileServiceImpl(EmployeeCertificationAuditRepository employeeCertificationAuditRepository, EmployeeCertificationsRepository employeeCertificationsRepository) {
        this.employeeCertificationAuditRepository = employeeCertificationAuditRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
    }


    @Override
    public EmployeeCertificationResponse uploadCertificate(
            Long employeeId,
            Long certificationId,
            MultipartFile file) {

        EmployeeCertification ec = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(employeeId, certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee Certification not found"));

        try {
            if (file.isEmpty()) {
                throw new BadRequestException("File cannot be empty");
            }

            // Store file in database
            ec.setCertificateFile(file.getBytes());
            ec.setFileName(file.getOriginalFilename());
            ec.setFileType(file.getContentType());

            EmployeeCertification saved = employeeCertificationsRepository.save(ec);

            EmployeeCertificationAudit employeeCertificationAudit = new EmployeeCertificationAudit();
            employeeCertificationAudit.setEmployeeId(employeeId);
            employeeCertificationAudit.setCertificationId(certificationId);
            employeeCertificationAudit.setAction(CertificationAuditActions.FILE_UPLOADED);
            employeeCertificationAudit.setFileName(file.getOriginalFilename());
            employeeCertificationAudit.setFileType(file.getContentType());
            employeeCertificationAudit.setFileData(file.getBytes());
            employeeCertificationAudit.setPerformedBy("SYSTEM");
            employeeCertificationAudit.setPerformedAt(LocalDateTime.now());
            employeeCertificationAudit.setRemarks("Certificate uploaded successfully");

            employeeCertificationAuditRepository.save(employeeCertificationAudit);

            return EmployeeCertificationResponse.builder()
                    .id(saved.getId())
                    .certificationName(saved.getCertification().getName())
                    .certificationNumber(saved.getCertificateNumber())
                    .issueDate(saved.getIssueDate())
                    .expiryDate(saved.getExpiryDate())
                    .status(saved.getStatus())
                    .proofUrl(saved.getProofUrl())
                    .fileName(saved.getFileName())
                    .fileType(saved.getFileType())
                    .fileSize(file.getSize())
                    .certificateUploaded(saved.getCertificateFile() != null)
                    .uploadedAt(saved.getUpdatedDate())
                    .isDeleted(saved.getIsDeleted())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error while uploading file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadCertificateResponse downloadCertificate(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                        employeeId,
                        certificationId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee certification not found"
                        )
                );

        // Correct validation
        if (certification.getCertificateFile() == null
                || certification.getCertificateFile().length == 0) {
            throw new ResourceNotFoundException(
                    "No certificate file has been uploaded for this certification"
            );
        }

        return DownloadCertificateResponse.builder()
                .fileName(certification.getFileName())
                .contentType(
                        certification.getFileType() != null
                                ? certification.getFileType()
                                : MediaType.APPLICATION_OCTET_STREAM_VALUE
                )
                .fileData(certification.getCertificateFile())
                .fileSize(certification.getCertificateFile().length)
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
                                employeeId,
                                certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee certification not found"));

        if (certification.getCertificateFile() == null
                || certification.getCertificateFile().length == 0) {
            throw new ResourceNotFoundException(
                    "No certificate file available for preview");
        }

        return PreviewCertificateResponse.builder()
                .message("Certificate preview loaded successfully")
                .fileName(certification.getFileName())
                .contentType(
                        certification.getFileType() != null
                                ? certification.getFileType()
                                : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .fileData(certification.getCertificateFile())
                .fileSize(certification.getCertificateFile().length)
                .build();
    }

    @Override
    @Transactional
    public void deleteCertificateFile(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId,
                                certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee certification not found"));

        if (certification.getCertificateFile() == null
                || certification.getCertificateFile().length == 0) {
            throw new ResourceNotFoundException(
                    "No uploaded certificate found");
        }

        String deletedFileName = certification.getFileName();

        certification.setCertificateFile(null);
        certification.setFileName(null);
        certification.setFileType(null);

        employeeCertificationsRepository.save(certification);

        EmployeeCertificationAudit audit =
                new EmployeeCertificationAudit();

        audit.setEmployeeId(employeeId);
        audit.setCertificationId(certificationId);
        audit.setAction(CertificationAuditActions.FILE_DELETED);
        audit.setFileName(deletedFileName);
        audit.setPerformedBy("SYSTEM");
        audit.setPerformedAt(LocalDateTime.now());
        audit.setRemarks("Certificate file deleted successfully");

        employeeCertificationAuditRepository.save(audit);
    }

    @Override
    @Transactional
    public EmployeeCertificationResponse replaceCertificateFile(
            Long employeeId,
            Long certificationId,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        EmployeeCertification certification =
                employeeCertificationsRepository
                        .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                                employeeId,
                                certificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee certification not found"));

        String oldFileName = certification.getFileName();

        try {
            certification.setCertificateFile(file.getBytes());
            certification.setFileName(file.getOriginalFilename());
            certification.setFileType(file.getContentType());

            EmployeeCertification saved =
                    employeeCertificationsRepository.save(certification);

            EmployeeCertificationAudit audit =
                    new EmployeeCertificationAudit();

            audit.setEmployeeId(employeeId);
            audit.setCertificationId(certificationId);
            audit.setAction(CertificationAuditActions.FILE_REPLACED);
            audit.setFileName(file.getOriginalFilename());
            audit.setFileType(file.getContentType());
            audit.setFileData(file.getBytes());
            audit.setPerformedBy("SYSTEM");
            audit.setPerformedAt(LocalDateTime.now());
            audit.setRemarks(
                    (oldFileName != null && !oldFileName.isBlank())
                            ? "Certificate replaced. Old file: " + oldFileName
                            : "Certificate uploaded for the first time via replace operation"
            );
            employeeCertificationAuditRepository.save(audit);

            return EmployeeCertificationResponse.builder()
                    .id(saved.getId())
                    .certificationName(saved.getCertification().getName())
                    .certificationNumber(saved.getCertificateNumber())
                    .issueDate(saved.getIssueDate())
                    .expiryDate(saved.getExpiryDate())
                    .status(saved.getStatus())
                    .proofUrl(saved.getProofUrl())
                    .fileName(saved.getFileName())
                    .fileType(saved.getFileType())
                    .fileSize(file.getSize())
                    .certificateUploaded(true)
                    .uploadedAt(saved.getUpdatedDate())
                    .isDeleted(saved.getIsDeleted())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to replace certificate file", e);
        }
    }
}
