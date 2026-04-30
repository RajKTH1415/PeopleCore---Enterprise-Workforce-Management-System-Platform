package com.peoplecore.service;

import com.peoplecore.dto.response.CertificationVerificationResponse;
import com.peoplecore.dto.response.EmployeeCertificationAuditResponse;

import java.util.List;

public interface EmployeeCertificationAuditService {

    List<EmployeeCertificationAuditResponse> getCertificationHistory(
            Long employeeId,
            Long certificationId);

    CertificationVerificationResponse getVerificationDetails(
            Long employeeId,
            Long certificationId);
}
