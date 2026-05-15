package com.peoplecore.repository;

import com.peoplecore.module.CityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityMaster, Long> {

    List<CityMaster> findByStateId(Long stateId);

    Optional<CityMaster> findByNameIgnoreCase(String name);

    Optional<CityMaster> findByNameIgnoreCaseAndStateId(String city, Long id);
}
