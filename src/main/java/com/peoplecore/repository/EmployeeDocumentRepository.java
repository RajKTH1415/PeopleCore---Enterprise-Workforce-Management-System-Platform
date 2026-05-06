package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> , JpaSpecificationExecutor {



    Optional<EmployeeDocument> findByEmployeeIdAndFileHash(Long employeeId, String fileHash);


    Optional<EmployeeDocument> findByEmployeeIdAndDocumentId(Long employeeId, String documentId);

    Optional<EmployeeDocument> findByDocumentIdAndIsDeletedFalse(String documentId);

    Optional<EmployeeDocument> findByDocumentId(String documentId);


}