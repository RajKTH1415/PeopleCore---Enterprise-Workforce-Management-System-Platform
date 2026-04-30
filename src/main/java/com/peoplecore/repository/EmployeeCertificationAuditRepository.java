package com.peoplecore.repository;

import com.peoplecore.module.EmployeeCertificationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertificationAuditRepository
        extends JpaRepository<EmployeeCertificationAudit, Long> {

    List<EmployeeCertificationAudit>
    findByEmployeeIdAndCertificationIdOrderByPerformedAtDesc(
            Long employeeId,
            Long certificationId);
}
