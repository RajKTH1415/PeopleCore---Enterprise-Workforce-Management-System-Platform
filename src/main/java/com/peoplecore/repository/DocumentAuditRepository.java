package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocumentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAuditRepository extends JpaRepository<EmployeeDocumentAudit, Long> {
    void deleteByDocumentId(Long id);


    List<EmployeeDocumentAudit> findByDocumentIdOrderByPerformedAtDesc(Long documentId);
}