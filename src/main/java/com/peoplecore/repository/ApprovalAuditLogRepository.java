package com.peoplecore.repository;

import com.peoplecore.module.ApprovalAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalAuditLogRepository extends JpaRepository<ApprovalAuditLog, Long> {


    Page<ApprovalAuditLog> findByApprovalId(Long approvalId, Pageable pageable);
}
