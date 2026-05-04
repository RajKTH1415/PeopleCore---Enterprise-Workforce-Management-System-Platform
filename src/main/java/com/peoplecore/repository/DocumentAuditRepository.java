package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocumentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentAuditRepository extends JpaRepository<EmployeeDocumentAudit, Long> {}