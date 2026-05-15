package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.BulkAssignResponse;
import com.peoplecore.dto.response.CertificationResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.*;
import com.peoplecore.enums.CertificationStatus;
import com.peoplecore.repository.*;
import com.peoplecore.service.EmployeeCertificationsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class EmployeeCertificationsServiceImpl implements EmployeeCertificationsService {

    private final EmployeeCertificationsRepository employeeCertificationsRepository;
    private final CertificationRepository certificationRepository;
    private final EmployeeRepository employeeRepository;
   private final EmployeeCertificationAuditRepository employeeCertificationAuditRepository;

    public EmployeeCertificationsServiceImpl(EmployeeCertificationsRepository employeeCertificationsRepository, CertificationRepository certificationRepository, EmployeeRepository employeeRepository, EmployeeCertificationAuditRepository employeeCertificationAuditRepository) {
        this.employeeCertificationsRepository = employeeCertificationsRepository;
        this.certificationRepository = certificationRepository;
        this.employeeRepository = employeeRepository;
        this.employeeCertificationAuditRepository = employeeCertificationAuditRepository;

    }


    @Override
    public EmployeeCertificationResponse assignCertification(Long empId, AssignCertificationRequest assignCertificationRequest) {

      Employee employee =  employeeRepository.findById(empId)
                   .orElseThrow(()-> new RuntimeException("Employee not found with ID :"+ empId));

        Certification certi = certificationRepository.findByIdAndIsDeletedFalse(assignCertificationRequest.getCertificateId())
                .orElseThrow(()-> new RuntimeException("Certification not found or Deleted with ID :"+ assignCertificationRequest.getCertificateId()));
      boolean isExists =    employeeCertificationsRepository.existsByEmployeeIdAndCertificationIdAndIsDeletedFalse(empId, assignCertificationRequest.getCertificateId());
     if (isExists){
         throw new RuntimeException("Certification already assigned");
     }

     EmployeeCertification certification = EmployeeCertification.builder()
             .employee(employee)
             .certification(certi)
             .certificateNumber(assignCertificationRequest.getCertificateNumber())
             .issueDate(assignCertificationRequest.getIssueDate())
             .expiryDate(assignCertificationRequest.getExpiryDate())
             .proofUrl(assignCertificationRequest.getProofUrl())
             .isDeleted(false)
             .status(calculateStatus(assignCertificationRequest.getExpiryDate()))
             .build();

      EmployeeCertification savedEmployeeCertificate =   employeeCertificationsRepository.save(certification);

        return EmployeeCertificationResponse.builder()
                .id(savedEmployeeCertificate.getId())
                .certificationNumber(savedEmployeeCertificate.getCertificateNumber())
                .issueDate(savedEmployeeCertificate.getIssueDate())
                .expiryDate(savedEmployeeCertificate.getExpiryDate())
                .status(savedEmployeeCertificate.getStatus())
                .isDeleted(savedEmployeeCertificate.getIsDeleted())
                .proofUrl(savedEmployeeCertificate.getProofUrl())
                .build();
    }

    @Override
    public PageResponse<EmployeeCertificationResponse> getEmployeeCertifications(Long empId, int page, int size, String status, String sortBy, String direction) {

    Employee employee =  employeeRepository.findById(empId)
                 .orElseThrow(()-> new RuntimeException("Employee not found with ID :"+ empId));
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<EmployeeCertification> result = employeeCertificationsRepository.findByEmployeeIdAndFilters(empId,status, pageable);

        List<EmployeeCertificationResponse> content = result.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<EmployeeCertificationResponse>builder()
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
                .direction(direction)
                .build();
    }

    @Override
    public EmployeeCertificationResponse updateEmployeeCertification(Long employeeId, Long certificationId, UpdateEmployeeCertificationRequest request) {
        EmployeeCertification ec = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(employeeId, certificationId)
                .orElseThrow(() -> new RuntimeException("Employee Certification not found"));

        if (request.getIssueDate() != null) {
            ec.setIssueDate(request.getIssueDate());
        }

        if (request.getExpiryDate() != null) {
            ec.setExpiryDate(request.getExpiryDate());
        }

        if (request.getProofUrl() != null) {
            ec.setProofUrl(request.getProofUrl());
        }
        if (request.getCertificateNumber() != null){
            ec.setCertificateNumber(request.getCertificateNumber());
        }
        ec.setStatus(calculateStatus(ec.getExpiryDate()));

        EmployeeCertification updatedEmployeeCertification = employeeCertificationsRepository.save(ec);

        return EmployeeCertificationResponse.builder()
                .id(updatedEmployeeCertification.getId())
                .certificationNumber(updatedEmployeeCertification.getCertificateNumber())
                .issueDate(updatedEmployeeCertification.getIssueDate())
                .expiryDate(updatedEmployeeCertification.getExpiryDate())
                .status(updatedEmployeeCertification.getStatus())
                .proofUrl(updatedEmployeeCertification.getProofUrl())
                .isDeleted(updatedEmployeeCertification.getIsDeleted())
                .build();
    }

    @Override
    public EmployeeCertificationResponse removeEmployeeCertification(Long employeeId, Long certificationId) {
        EmployeeCertification ec = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(employeeId, certificationId)
                .orElseThrow(() ->
                        new RuntimeException("Employee Certification not found"));

        ec.setIsDeleted(true);
        ec.setStatus(CertificationStatus.REMOVED.name());

       EmployeeCertification savedEmployeeCertifications =  employeeCertificationsRepository.save(ec);

        return EmployeeCertificationResponse.builder()
                .id(savedEmployeeCertifications.getId())
                .employeeId(savedEmployeeCertifications.getId())
                .status(savedEmployeeCertifications.getStatus())
                .issueDate(savedEmployeeCertifications.getIssueDate())
                .expiryDate(savedEmployeeCertifications.getExpiryDate())
                .isDeleted(savedEmployeeCertifications.getIsDeleted())
                .certificationNumber(savedEmployeeCertifications.getCertificateNumber())
                .proofUrl(savedEmployeeCertifications.getProofUrl())
                .build();
    }

    @Override
    public List<EmployeeCertificationResponse> getExpiredCertifications(
            Long employeeId,
            LocalDate expiredBefore) {

        LocalDate dateToUse = (expiredBefore != null)
                ? expiredBefore
                : LocalDate.now();

        List<EmployeeCertification> list =
                employeeCertificationsRepository.findExpiredCertifications(
                        dateToUse,
                        employeeId
                );

        return list.stream()
                .map(ec -> EmployeeCertificationResponse.builder()
                        .id(ec.getId())
                        .employeeName(
                                ec.getEmployee() != null
                                        ? ec.getEmployee().getFirstName() + " " + ec.getEmployee().getLastName()
                                        : null
                        )
                        .certificationNumber(ec.getCertificateNumber())
                        .issueDate(ec.getIssueDate())
                        .expiryDate(ec.getExpiryDate())
                        .status(
                                ec.getExpiryDate() != null &&
                                        ec.getExpiryDate().isBefore(LocalDate.now())
                                        ? "EXPIRED"
                                        : ec.getStatus()
                        )
                        .proofUrl(ec.getProofUrl())
                        .isDeleted(ec.getIsDeleted())
                        .build()
                )
                .toList();
    }

    @Override
    public BulkAssignResponse bulkAssignCertification(BulkAssignCertificationRequest request) {

        Certification cert = certificationRepository
                .findByIdAndIsDeletedFalse(request.getCertificationId())
                .orElseThrow(() -> new RuntimeException("Certification not found or deleted"));

        List<Long> successIds = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Long empId : request.getEmployeeIds()) {
            try {

                Employee employee = employeeRepository.findById(empId)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

                boolean exists = employeeCertificationsRepository
                        .existsByEmployeeIdAndCertificationIdAndIsDeletedFalse(empId, request.getCertificationId());

                if (exists) {
                    failed.add(empId + " - already assigned");
                    continue;
                }

                EmployeeCertification ec = EmployeeCertification.builder()
                        .employee(employee)
                        .certification(cert)
                        .certificateNumber(request.getCertificateNumber())
                        .issueDate(request.getIssueDate())
                        .expiryDate(request.getExpiryDate())
                        .proofUrl(request.getProofUrl())
                        .isDeleted(false)
                        .status(calculateStatus(request.getExpiryDate()))
                        .build();

                employeeCertificationsRepository.save(ec);

                successIds.add(empId);

            } catch (Exception e) {
                failed.add(empId + " - " + e.getMessage());
            }
        }

        return BulkAssignResponse.builder()
                .totalRequested(request.getEmployeeIds().size())
                .successCount(successIds.size())
                .failedCount(failed.size())
                .successEmployeeIds(successIds)
                .failedRecords(failed)
                .build();
    }

    @Override
    public List<EmployeeCertificationResponse> getExpiringSoon(int days, Long employeeId) {

        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);

        List<EmployeeCertification> list =
                employeeCertificationsRepository.findExpiringSoon(
                        today,
                        futureDate,
                        employeeId
                );

        return list.stream()
                .map(ec -> EmployeeCertificationResponse.builder()
                        .id(ec.getId())
                        .employeeName(ec.getEmployee().getFirstName() + " " + ec.getEmployee().getLastName())
                        .certificationNumber(ec.getCertificateNumber())
                        .issueDate(ec.getIssueDate())
                        .expiryDate(ec.getExpiryDate())
                        .status(ec.getStatus())
                        .proofUrl(ec.getProofUrl())
                        .isDeleted(ec.getIsDeleted())
                        .build()
                )
                .toList();
    }

    @Override
    @Transactional
    public EmployeeCertificationResponse renewCertification(
            Long employeeId,
            Long certificationId,
            RenewCertificationRequest request) {

        EmployeeCertification ec = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(employeeId, certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee Certification not found"));

        // Update renewal fields
        if (request.getIssueDate() != null) {
            ec.setIssueDate(request.getIssueDate());
        }

        if (request.getExpiryDate() != null) {
            ec.setExpiryDate(request.getExpiryDate());
        }

        if (request.getProofUrl() != null) {
            ec.setProofUrl(request.getProofUrl());
        }

        if (request.getCertificateNumber() != null) {
            ec.setCertificateNumber(request.getCertificateNumber());
        }

        // Reset / update status after renewal
        ec.setStatus(calculateStatus(request.getExpiryDate() != null
                ? request.getExpiryDate()
                : ec.getExpiryDate()));

        EmployeeCertification saved = employeeCertificationsRepository.save(ec);

        return EmployeeCertificationResponse.builder()
                .id(saved.getId())
                .certificationNumber(saved.getCertificateNumber())
                .issueDate(saved.getIssueDate())
                .expiryDate(saved.getExpiryDate())
                .status(saved.getStatus())
                .proofUrl(saved.getProofUrl())
                .isDeleted(saved.getIsDeleted())
                .build();
    }

    @Override
    public EmployeeCertificationResponse verifyCertification(Long employeeId, Long certificationId) {

        EmployeeCertification ec = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(employeeId, certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee Certification not found"));

        LocalDate today = LocalDate.now();

        if (ec.getExpiryDate() != null && ec.getExpiryDate().isBefore(today)) {
            throw new BadRequestException("Cannot verify expired certification");
        }

        ec.setStatus(CertificationStatus.VERIFIED.name());

        //  NEW AUDIT FIELDS
        ec.setVerifiedBy("SYSTEM"); // later replace with logged-in user
        ec.setVerifiedDate(today);
        ec.setVerificationNotes("Certification verified after validation");

        EmployeeCertification saved = employeeCertificationsRepository.save(ec);


        return EmployeeCertificationResponse.builder()
                .id(saved.getId())
                .certificationNumber(saved.getCertificateNumber())
                .issueDate(saved.getIssueDate())
                .expiryDate(saved.getExpiryDate())
                .status(saved.getStatus())
                .proofUrl(saved.getProofUrl())
                .isDeleted(saved.getIsDeleted())

                //  NEW FIELDS
                .verifiedBy(saved.getVerifiedBy())
                .verifiedDate(saved.getVerifiedDate())
                .verificationNotes(saved.getVerificationNotes())

                .build();
    }

    @Override
    public EmployeeCertificationResponse rejectCertification(Long employeeId, Long certificationId) {

        EmployeeCertification entity = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationId(employeeId, certificationId)
                .orElseThrow(() ->
                        new RuntimeException("Employee Certification not found")
                );

        // Optional business rule validation
        if (entity.getStatus() == CertificationStatus.REJECTED.name()) {
            throw new RuntimeException("Certification is already rejected");
        }

        if (entity.getStatus() == CertificationStatus.VERIFIED.name()) {
            throw new RuntimeException("Verified certification cannot be rejected");
        }

        // Update status
        entity.setStatus(CertificationStatus.REJECTED.name());



        EmployeeCertification saved = employeeCertificationsRepository.save(entity);

        EmployeeCertificationAudit audit = EmployeeCertificationAudit.builder()
                .employeeId(employeeId)
                .certificationId(certificationId)
                .action("REJECT")
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .fileUrl(entity.getFileUrl())
                .performedBy("SYSTEM")
                .performedAt(LocalDateTime.now())
                .remarks("Certification rejected")
                .build();
        employeeCertificationAuditRepository.save(audit);

        return mapToResponse(saved);
    }

    private EmployeeCertificationResponse mapToResponse(EmployeeCertification ec) {
        return EmployeeCertificationResponse.builder()
                .id(ec.getId())
                .certificationNumber(ec.getCertificateNumber())
                .issueDate(ec.getIssueDate())
                .expiryDate(ec.getExpiryDate())
                .status(calculateStatus(ec.getExpiryDate()))
                .proofUrl(ec.getProofUrl())
                .isDeleted(ec.getIsDeleted())
                .build();
    }


    private String calculateStatus(LocalDate expiryDate) {

        if (expiryDate == null) {
            return CertificationStatus.ACTIVE.name();
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            return CertificationStatus.EXPIRED.name();
        }

        return CertificationStatus.ACTIVE.name();
    }
}
