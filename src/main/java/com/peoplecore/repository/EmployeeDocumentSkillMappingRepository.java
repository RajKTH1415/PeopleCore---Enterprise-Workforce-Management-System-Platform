package com.peoplecore.repository;

import com.peoplecore.module.EmployeeDocumentSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentSkillMappingRepository extends JpaRepository<EmployeeDocumentSkillMapping, Long> {
    void deleteByDocumentId(Long id);
}