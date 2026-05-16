package com.peoplecore.repository;

import com.peoplecore.module.AddressHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressHistoryRepository
        extends JpaRepository<AddressHistory, Long> {

    List<AddressHistory> findByEmployeeIdOrderByChangedAtDesc(Long employeeId);

    List<AddressHistory> findByAddress_IdOrderByChangedAtDesc(Long addressId);
}
