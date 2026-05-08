package com.peoplecore.repository;

import com.peoplecore.module.DocumentApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentApprovalRepository
        extends JpaRepository<DocumentApproval, Long> {
}