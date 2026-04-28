package com.peoplecore.repository;

import com.peoplecore.module.EmployeeCertification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeCertificationsRepository extends JpaRepository<EmployeeCertification, Long> {


    Optional<EmployeeCertification> findById(Long aLong);

    boolean existsByEmployeeIdAndCertificationIdAndIsDeletedFalse(
            Long employeeId,
            Long certificationId
    );

    @Query("""
    SELECT ec FROM EmployeeCertification ec
    WHERE ec.employee.id = :empId
    AND ec.isDeleted = false
    AND (:status IS NULL OR ec.status = :status)
""")
    Page<EmployeeCertification> findByEmployeeIdAndFilters(
            @Param("empId") Long empId,
            @Param("status") String status,
            Pageable pageable
    );



    Optional<EmployeeCertification> findByEmployeeIdAndCertificationIdAndIsDeletedFalse(
            Long employeeId,
            Long certificationId
    );

    @Query("""
    SELECT ec FROM EmployeeCertification ec
    WHERE ec.isDeleted = false
    AND ec.expiryDate IS NOT NULL
    AND ec.expiryDate < :expiredBefore
    AND (:employeeId IS NULL OR ec.employee.id = :employeeId)
""")
    List<EmployeeCertification> findExpiredCertifications(
            @Param("expiredBefore") LocalDate expiredBefore,
            @Param("employeeId") Long employeeId
    );


    @Query("""
    SELECT ec FROM EmployeeCertification ec
    WHERE ec.isDeleted = false
      AND ec.expiryDate BETWEEN :today AND :futureDate
      AND (:employeeId IS NULL OR ec.employee.id = :employeeId)
""")
    List<EmployeeCertification> findExpiringSoon(
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate,
            @Param("employeeId") Long employeeId
    );

   List<EmployeeCertification> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

    @Query("""
    SELECT ec
    FROM EmployeeCertification ec
    WHERE ec.isDeleted = false
      AND ec.status = 'ACTIVE'
      AND ec.expiryDate IS NOT NULL
      AND ec.expiryDate < CURRENT_DATE
""")
    List<EmployeeCertification> findAllExpiredActiveCertifications();
}
