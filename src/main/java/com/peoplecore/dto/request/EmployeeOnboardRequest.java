package com.peoplecore.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeOnboardRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String designation;
    private String department;

    private String managerEmployeeId;
    private LocalDate joiningDate;

    private Integer userId; //optional
}