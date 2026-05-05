package com.peoplecore.repository;

import com.peoplecore.enums.ProficiencyLevel;
import com.peoplecore.module.EmployeeSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {


    boolean existsByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    @Query("""
            SELECT COUNT(es) > 0
            FROM EmployeeSkill es
            WHERE es.employee.id = :employeeId
              AND es.skill.id = :skillId
              AND es.isDeleted = false
            """)
    boolean existsEmployeeSkill(@Param("employeeId") Long employeeId,
                                @Param("skillId") Long skillId);


    boolean existsByEmployee_IdAndSkill_IdAndIsDeletedFalse(
            Long employeeId,
            Long skillId
    );

    Optional<EmployeeSkill> findByEmployee_IdAndSkill_IdAndIsDeletedFalse(
            Long employeeId,
            Long skillId
    );


    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.skill.id = :skillId
      AND es.isDeleted = false
      AND (
            :proficiencyLevel IS NULL
            OR es.proficiencyLevel = :proficiencyLevel
          )
      AND (
            :verified IS NULL
            OR es.isVerified = :verified
          )
      AND (
            :category IS NULL
            OR LOWER(es.skill.category) = LOWER(:category)
          )
""")
    Page<EmployeeSkill> findEmployeesBySkillWithFilters(
            @Param("skillId") Long skillId,
            @Param("proficiencyLevel") ProficiencyLevel proficiencyLevel,
            @Param("verified") Boolean verified,
            @Param("category") String category,
            Pageable pageable
    );


    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.employee.id = :employeeId
      AND es.isDeleted = false
      AND (
            :proficiencyLevel IS NULL
            OR es.proficiencyLevel = :proficiencyLevel
          )
      AND (
            :verified IS NULL
            OR es.isVerified = :verified
          )
      AND (
            COALESCE(:search, '') = ''
            OR LOWER(CAST(es.skill.name AS string))
                LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
            OR LOWER(CAST(es.skill.category AS string))
                LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
          )
""")
    Page<EmployeeSkill> findEmployeeSkillsWithFilters(
            @Param("employeeId") Long employeeId,
            @Param("proficiencyLevel") ProficiencyLevel proficiencyLevel,
            @Param("verified") Boolean verified,
            @Param("search") String search,
            Pageable pageable
    );


    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.employee.id = :employeeId
      AND es.skill.id = :skillId
""")
    Optional<EmployeeSkill> findByEmployeeIdAndSkillIdIncludingDeleted(
            @Param("employeeId") Long employeeId,
            @Param("skillId") Long skillId);

    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.employee.id = :employeeId
      AND es.skill.id IN :skillIds
""")
    List<EmployeeSkill> findAllByEmployeeIdAndSkillIdsIncludingDeleted(
            @Param("employeeId") Long employeeId,
            @Param("skillIds") List<Long> skillIds
    );
    List<EmployeeSkill> findByEmployeeIdAndIsDeletedTrue(Long employeeId);

    @Modifying
    @Query("""
    DELETE FROM EmployeeSkill es
    WHERE es.employee.id = :employeeId
      AND es.skill.id = :skillId
""")
    int permanentlyDeleteByEmployeeIdAndSkillId(
            @Param("employeeId") Long employeeId,
            @Param("skillId") Long skillId
    );

    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.employee.id = :employeeId
      AND es.skill.id IN :skillIds
      AND es.isDeleted = false
""")
    List<EmployeeSkill> findByEmployeeIdAndSkillIds(
            @Param("employeeId") Long employeeId,
            @Param("skillIds") List<Long> skillIds
    );

    List<EmployeeSkill> findByEmployee_IdAndSkill_IdInAndIsDeletedFalse(
            Long employeeId,
            List<Long> skillIds
    );

    List<EmployeeSkill> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.skill.id IN :skillIds
      AND es.isDeleted = false
      AND es.proficiencyLevel IN :proficiencyLevels
""")
    Page<EmployeeSkill> findEmployeesByAnySkills(
            @Param("skillIds") List<Long> skillIds,
            @Param("proficiencyLevels") List<ProficiencyLevel> proficiencyLevels,
            Pageable pageable
    );

    @Query("""
    SELECT es
    FROM EmployeeSkill es
    WHERE es.employee.id IN (
        SELECT es2.employee.id
        FROM EmployeeSkill es2
        WHERE es2.skill.id IN :skillIds
          AND es2.isDeleted = false
          AND (
                COALESCE(:proficiencyLevels, NULL) IS NULL
                OR es2.proficiencyLevel IN :proficiencyLevels
              )
        GROUP BY es2.employee.id
        HAVING COUNT(DISTINCT es2.skill.id) = :skillCount
    )
    AND es.skill.id IN :skillIds
    AND es.isDeleted = false
""")
    Page<EmployeeSkill> findEmployeesByAllSkills(
            @Param("skillIds") List<Long> skillIds,
            @Param("skillCount") Long skillCount,
            @Param("proficiencyLevels") List<ProficiencyLevel> proficiencyLevels,
            Pageable pageable
    );

    Optional<EmployeeSkill> findByEmployeeIdAndSkillNameIgnoreCase(Long employeeId, String skillName);
}
