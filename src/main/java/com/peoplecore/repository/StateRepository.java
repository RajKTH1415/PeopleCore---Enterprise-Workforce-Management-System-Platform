package com.peoplecore.repository;

import com.peoplecore.module.StateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateMaster, Long> {

    List<StateMaster> findByCountryId(Long countryId);


    Optional<StateMaster> findByNameIgnoreCase(String state);

    Optional<StateMaster> findByNameIgnoreCaseAndCountryId(String state, Long id);
}
