package com.peoplecore.repository;

import com.peoplecore.module.DocumentApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentApprovalRepository
        extends JpaRepository<DocumentApproval, Long> {


    List<DocumentApproval> findByDocumentIdOrderByRequestedAtDesc(
            String documentId
    );

    Page<DocumentApproval> findByApprovalStatus(
            String approvalStatus,
            Pageable pageable);
}