package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {



    Optional<EmployeeDocument> findByEmployeeIdAndFileHash(Long employeeId, String fileHash);


}