package com.peoplecore.repository;


import com.peoplecore.module.DocumentApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentApprovalWorkflowRepository
        extends JpaRepository<DocumentApprovalWorkflow, Long> {

    List<DocumentApprovalWorkflow> findByDocumentIdOrderByApprovalLevelAsc(
            String documentId
    );
}
