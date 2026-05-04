package com.peoplecore.repository;

import com.peoplecore.enums.EmploymentStatus;
import com.peoplecore.module.Employee;
import com.peoplecore.enums.Status;
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
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
    List<Employee> findByManager(Employee manager);

@Query("""
SELECT e FROM Employee e
LEFT JOIN e.manager m
WHERE (:status IS NULL OR e.status = :status)
AND (:department IS NULL OR e.department LIKE CONCAT('%', CAST(:department AS string), '%'))
AND (:designation IS NULL OR e.designation LIKE CONCAT('%', CAST(:designation AS string), '%'))
AND (:managerId IS NULL OR m.employeeId = :managerId)
AND (
    :search IS NULL OR
    e.firstName LIKE CONCAT('%', CAST(:search AS string), '%') OR
    e.lastName LIKE CONCAT('%', CAST(:search AS string), '%') OR
    e.email LIKE CONCAT('%', CAST(:search AS string), '%')
)
""")
    Page<Employee> findEmployeesWithFilters(
            @Param("status") Status status,
            @Param("department") String department,
            @Param("designation") String designation,
            @Param("managerId") String managerId,
            @Param("search") String search,
            Pageable pageable
    );


    @Query("""
SELECT e FROM Employee e
LEFT JOIN FETCH e.manager
WHERE e.employeeId = :employeeId
""")
    Optional<Employee> findEmployeeWithManager(@Param("employeeId") String employeeId);

    @Query("SELECT e FROM Employee e WHERE e.manager.employeeId = :employeeId")
    List<Employee> findSubordinates(@Param("employeeId") String employeeId);


    Long countByIsDeletedFalse();

    Long countByEmploymentStatusAndIsDeletedFalse(EmploymentStatus status);



}
