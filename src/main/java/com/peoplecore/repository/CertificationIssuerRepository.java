package com.peoplecore.repository;



import com.peoplecore.module.CertificationIssuer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificationIssuerRepository
        extends JpaRepository<CertificationIssuer, Long> {

    Optional<CertificationIssuer> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
