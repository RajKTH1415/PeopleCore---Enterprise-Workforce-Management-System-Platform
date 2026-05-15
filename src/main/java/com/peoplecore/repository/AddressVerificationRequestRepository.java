package com.peoplecore.repository;

import com.peoplecore.module.AddressVerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressVerificationRequestRepository
        extends JpaRepository<AddressVerificationRequest, Long> {

    List<AddressVerificationRequest>
    findByVerificationStatus(String status);

}
