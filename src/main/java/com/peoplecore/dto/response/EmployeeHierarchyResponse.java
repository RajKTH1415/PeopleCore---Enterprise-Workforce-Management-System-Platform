package com.peoplecore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeHierarchyResponse {

    private Long id;
    private String employeeId;
    private String fullName;

    private EmployeeHierarchyResponse manager; // upward
    private List<EmployeeHierarchyResponse> subordinates; // downward
}