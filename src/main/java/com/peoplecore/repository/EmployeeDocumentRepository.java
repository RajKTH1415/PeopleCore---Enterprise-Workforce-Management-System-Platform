package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> , JpaSpecificationExecutor {



    Optional<EmployeeDocument> findByEmployeeIdAndFileHash(Long employeeId, String fileHash);


    Optional<EmployeeDocument> findByEmployeeIdAndDocumentId(Long employeeId, String documentId);

    Optional<EmployeeDocument> findByDocumentIdAndIsDeletedFalse(String documentId);

    Optional<EmployeeDocument> findByDocumentId(String documentId);

    @Modifying
    @Query("UPDATE EmployeeDocument d SET d.isPrimary = false WHERE d.employeeId = :employeeId")
    void clearPrimaryForEmployee(@Param("employeeId") Long employeeId);


}