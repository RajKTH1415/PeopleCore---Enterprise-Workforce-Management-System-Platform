package com.peoplecore.repository;

import com.peoplecore.module.EmployeeAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAddressRepository
        extends JpaRepository<EmployeeAddress, Long> {

    List<EmployeeAddress> findByEmployeeIdAndIsDeletedFalse(Long employeeId);

    Optional<EmployeeAddress> findByEmployeeIdAndIsPrimaryTrue(Long employeeId);

    List<EmployeeAddress> findByEmployeeIdAndAddressType(
            Long employeeId,
            String addressType
    );

}
