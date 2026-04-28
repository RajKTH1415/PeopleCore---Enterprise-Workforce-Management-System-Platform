package com.peoplecore.service;

import com.peoplecore.dto.request.AssignCertificationRequest;
import com.peoplecore.dto.request.BulkAssignCertificationRequest;
import com.peoplecore.dto.request.RenewCertificationRequest;
import com.peoplecore.dto.request.UpdateEmployeeCertificationRequest;
import com.peoplecore.dto.response.BulkAssignResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PageResponse;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeCertificationsService {


    EmployeeCertificationResponse assignCertification(Long empId, AssignCertificationRequest assignCertificationRequest);

    PageResponse<EmployeeCertificationResponse> getEmployeeCertifications(Long empId,
                                                                          int page,
                                                                          int size,
                                                                          String status,
                                                                          String sortBy,
                                                                          String direction);

    EmployeeCertificationResponse updateEmployeeCertification(
            Long employeeId,
            Long certificationId,
            UpdateEmployeeCertificationRequest request);

    EmployeeCertificationResponse removeEmployeeCertification(Long employeeId, Long certificationId);

    List<EmployeeCertificationResponse> getExpiredCertifications(
            Long employeeId,
            LocalDate expiredBefore);

    BulkAssignResponse bulkAssignCertification(BulkAssignCertificationRequest request);

    List<EmployeeCertificationResponse> getExpiringSoon(int days, Long employeeId);

    EmployeeCertificationResponse renewCertification(
            Long employeeId,
            Long certificationId,
            RenewCertificationRequest request
    );

}
