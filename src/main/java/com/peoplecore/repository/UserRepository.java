package com.peoplecore.repository;

import com.peoplecore.enums.RoleName;
import com.peoplecore.enums.Status;
import com.peoplecore.module.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("""
           SELECT u FROM User u
           WHERE LOWER(u.userEmail) = LOWER(:userEmail)
              OR LOWER(u.userName) = LOWER(:userName)
           """)
    Optional<User> findExistingUser(@Param("userEmail") String userEmail,
                                    @Param("userName") String userName);

    Optional<User> findByUserID(String userID);

    Optional<User> findById(Integer id);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserName(String userName);

    boolean existsByMobileNumber(String userMobileNumber);

    @Query(value = "SELECT user_id FROM user_table ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastUserId();


    @Query("""
       SELECT DISTINCT u FROM User u
       LEFT JOIN u.roles r
       WHERE (:status IS NULL OR u.status = :status)
       AND (:role IS NULL OR r.name = :role)
       AND (:name IS NULL OR u.userName ILIKE CONCAT('%', :name, '%'))
       """)
    Page<User> findUsersWithFilters(
            @Param("status") Status status,
            @Param("role") RoleName role,
            @Param("name") String name,
            Pageable pageable
    );
}
