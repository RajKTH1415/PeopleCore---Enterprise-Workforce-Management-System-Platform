package com.peoplecore.repository;

import com.peoplecore.module.EmployeeCertificationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeCertificationAuditRepository
        extends JpaRepository<EmployeeCertificationAudit, Long> {
}
