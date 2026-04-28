package com.peoplecore.repository;

import com.peoplecore.module.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameAndIsDeletedFalse(String name);

    Optional<Skill> findById(Long id);

    Optional<Skill> findByIdAndIsDeletedFalse(Long id);


    Page<Skill> findByIsDeletedFalse(Pageable pageable);

    Page<Skill> findByCategoryIgnoreCaseAndIsDeletedFalse(
            String category,
            Pageable pageable
    );

    Page<Skill> findByNameContainingIgnoreCaseAndIsDeletedFalse(
            String name,
            Pageable pageable
    );

    Page<Skill> findByCategoryIgnoreCaseAndNameContainingIgnoreCaseAndIsDeletedFalse(
            String category,
            String name,
            Pageable pageable
    );

    List<Skill> findByIsDeletedFalse();
}