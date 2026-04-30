package com.peoplecore.service.Impl;


import com.peoplecore.dto.response.CertificationVerificationResponse;
import com.peoplecore.dto.response.EmployeeCertificationAuditResponse;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.EmployeeCertification;
import com.peoplecore.module.EmployeeCertificationAudit;
import com.peoplecore.repository.EmployeeCertificationAuditRepository;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import com.peoplecore.service.EmployeeCertificationAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class EmployeeCertificationAuditServiceImpl implements EmployeeCertificationAuditService {

    private final EmployeeCertificationAuditRepository employeeCertificationAuditRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;

    public EmployeeCertificationAuditServiceImpl(EmployeeCertificationAuditRepository employeeCertificationAuditRepository, EmployeeCertificationsRepository employeeCertificationsRepository) {
        this.employeeCertificationAuditRepository = employeeCertificationAuditRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
    }

    @Override
    public List<EmployeeCertificationAuditResponse> getCertificationHistory(Long employeeId, Long certificationId) {

      List<EmployeeCertificationAudit>  employeeCertificationAudits = employeeCertificationAuditRepository.findByEmployeeIdAndCertificationIdOrderByPerformedAtDesc(employeeId, certificationId);
      if (employeeCertificationAudits.isEmpty()){
          throw new RuntimeException("No certification audit history found");
      }

        return employeeCertificationAudits.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CertificationVerificationResponse getVerificationDetails(
            Long employeeId,
            Long certificationId) {

        EmployeeCertification certification = employeeCertificationsRepository
                .findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
                        employeeId,
                        certificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee certification not found"));

        return CertificationVerificationResponse.builder()
                .employeeId(certification.getEmployee().getId())
                .certificationId(certification.getCertification().getId())
                .certificationName(certification.getCertification().getName())
                .certificationNumber(certification.getCertificateNumber())
                .verificationStatus(certification.getStatus())
                .verifiedBy(certification.getVerifiedBy())
                .verifiedAt(
                        certification.getVerifiedDate() != null
                                ? certification.getVerifiedDate().atStartOfDay()
                                : null
                )
                .verificationNotes(certification.getVerificationNotes())
                .verified(certification.getVerifiedDate() != null)
                .build();
    }

    private EmployeeCertificationAuditResponse mapToResponse(
            EmployeeCertificationAudit audit) {

        return EmployeeCertificationAuditResponse.builder()
                .id(audit.getId())
                .employeeId(audit.getEmployeeId())
                .certificationId(audit.getCertificationId())
                .action(audit.getAction())
                .fileName(audit.getFileName())
                .fileType(audit.getFileType())
                .performedBy(audit.getPerformedBy())
                .performedAt(audit.getPerformedAt())
                .remarks(audit.getRemarks())
                .build();
    }
}

