package com.peoplecore.repository;


import com.peoplecore.module.EmployeeCertificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeCertificationHistoryRepository
        extends JpaRepository<EmployeeCertificationHistory, Long> {

    List<EmployeeCertificationHistory>
    findByEmployeeIdAndCertificationIdOrderByChangedAtDesc(
            Long employeeId,
            Long certificationId);
}
