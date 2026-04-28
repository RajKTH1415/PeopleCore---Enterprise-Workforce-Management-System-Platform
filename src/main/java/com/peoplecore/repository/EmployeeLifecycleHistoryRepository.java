package com.peoplecore.repository;

import com.peoplecore.module.EmployeeLifecycleHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EmployeeLifecycleHistoryRepository
        extends JpaRepository<EmployeeLifecycleHistory, Long> {

    Page<EmployeeLifecycleHistory> findByEmployee_Id(
            Long employeeId,
            Pageable pageable
    );
    Page<EmployeeLifecycleHistory> findByEmployee_IdAndNewStatusAndChangedAtBetween(
            Long employeeId,
            String status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}