package com.peoplecore.dto.request;

import com.peoplecore.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {

    private String employeeId;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String designation;
    private String department;
    private LocalDate joiningDate;
    private Status status;
    private String employeeManagerId;
}
