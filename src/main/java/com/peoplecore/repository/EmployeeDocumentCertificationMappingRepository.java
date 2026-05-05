package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocumentCertificationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentCertificationMappingRepository extends JpaRepository<EmployeeDocumentCertificationMapping, Long> {
    void deleteByDocumentId(Long id);

    boolean existsByDocumentIdAndEmployeeCertificationId(Long documentId, Long employeeCertificationId);
}
