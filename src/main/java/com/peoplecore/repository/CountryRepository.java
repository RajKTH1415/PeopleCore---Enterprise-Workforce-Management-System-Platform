package com.peoplecore.repository;

import com.peoplecore.module.CountryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryMaster, Long> {

    boolean existsByCode(String code);

    Optional<CountryMaster> findByCode(String code);

    Optional<CountryMaster> findByNameIgnoreCase(String name);



}