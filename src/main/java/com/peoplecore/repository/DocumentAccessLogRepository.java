package com.peoplecore.repository;

import com.peoplecore.module.DocumentAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentAccessLogRepository
        extends JpaRepository<DocumentAccessLog, Long> {
}