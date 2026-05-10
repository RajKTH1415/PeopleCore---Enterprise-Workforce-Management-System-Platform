package com.peoplecore.repository;

import com.peoplecore.module.DocumentApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentApprovalRepository
        extends JpaRepository<DocumentApproval, Long> {


    Page<DocumentApproval> findByDocumentId(
            String documentId,
            Pageable pageable
    );

    Page<DocumentApproval> findByApprovalStatus(
            String approvalStatus,
            Pageable pageable);

    long countByApprovalStatus(String approvalStatus);

    Page<DocumentApproval> findByRequestedBy(
            Long requestedBy,
            Pageable pageable
    );

    Page<DocumentApproval> findByApproverIdAndApprovalStatus(
            Long approverId,
            String approvalStatus,
            Pageable pageable
    );

    @Query("""
       SELECT AVG(
           EXTRACT(EPOCH FROM (d.approvedAt - d.requestedAt)) / 3600
       )
       FROM DocumentApproval d
       WHERE d.approvedAt IS NOT NULL
       """)
    Double getAverageApprovalTimeInHours();
}