package com.peoplecore.repository;

import com.peoplecore.module.DocumentVersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionHistory, Long> {
    void deleteByDocumentRefId(Long id);
}