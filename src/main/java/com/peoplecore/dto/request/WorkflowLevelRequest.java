package com.peoplecore.dto.request;


import lombok.Data;

@Data
public class WorkflowLevelRequest {

    private Integer approvalLevel;

    private Long approverId;

    private String roleName;
}
