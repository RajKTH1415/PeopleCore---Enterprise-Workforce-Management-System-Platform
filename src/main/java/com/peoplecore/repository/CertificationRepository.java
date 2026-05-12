package com.peoplecore.repository;

import com.peoplecore.module.Certification;
import com.peoplecore.module.EmployeeCertification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByNameAndIssuerAndIsDeletedFalse(String name, String issuer);

    Optional<Certification> findById(Long certificateId);

    @Query("""
    SELECT c.issuer, COUNT(c)
    FROM Certification c
    GROUP BY c.issuer
    ORDER BY COUNT(c) DESC
""")
    List<Object[]> findTopIssuers();

    @Query("""
    SELECT c.status, COUNT(c)
    FROM Certification c
    GROUP BY c.status
""")
    List<Object[]> countByStatus();

    boolean existsByNameIgnoreCaseAndIssuerIgnoreCase(
            String name,
            String issuer
    );


    @Query("""
           SELECT c
           FROM Certification c
           WHERE c.id = :id
           """)
    Optional<Certification> findCertificationIncludingDeleted(
            @Param("id") Long id
    );

    Optional<Certification> findByIdAndIsDeletedFalse(Long id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

@Query(value = """
    SELECT * FROM certifications c
    WHERE (:includeDeleted = true OR c.is_deleted = false)

    AND (:name IS NULL OR c.name ILIKE CONCAT('%', :name, '%'))
    AND (:issuer IS NULL OR c.issuer ILIKE CONCAT('%', :issuer, '%'))

    AND (
        :search IS NULL OR
        c.name ILIKE CONCAT('%', :search, '%') OR
        c.issuer ILIKE CONCAT('%', :search, '%')
    )
""",
        countQuery = """
    SELECT COUNT(*) FROM certifications c
    WHERE (:includeDeleted = true OR c.is_deleted = false)

    AND (:name IS NULL OR c.name ILIKE CONCAT('%', :name, '%'))
    AND (:issuer IS NULL OR c.issuer ILIKE CONCAT('%', :issuer, '%'))

    AND (
        :search IS NULL OR
        c.name ILIKE CONCAT('%', :search, '%') OR
        c.issuer ILIKE CONCAT('%', :search, '%')
    )
""",
        nativeQuery = true)
Page<Certification> findCertificationsWithFilters(
        @Param("includeDeleted") boolean includeDeleted,
        @Param("name") String name,
        @Param("issuer") String issuer,
        @Param("search") String search,
        Pageable pageable
);


    Optional<Certification> findByNameIgnoreCase(String title);
}