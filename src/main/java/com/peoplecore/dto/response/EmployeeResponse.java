package com.peoplecore.dto.response;
import com.peoplecore.enums.EmploymentStatus;
import com.peoplecore.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String employeeId;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String designation;
    private String department;
    private Status status;
    private String terminationReason;

    private LocalDate noticeStartDate;
    private LocalDate exitDate;

    private EmploymentStatus employmentStatus;

    private LocalDate joiningDate;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;

    private Manager manager;
    private List<Subordinate> subordinates;
    private User user;

    //  MANAGER
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Manager {
        private Long id;
        private String employeeId;
        private String fullName;
    }

    //  SUBORDINATE
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Subordinate {
        private Long id;
        private String employeeId;
        private String fullName;
    }

    // USER
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Integer userId;
        private String username;
        private String role;
        private String status;
    }
}