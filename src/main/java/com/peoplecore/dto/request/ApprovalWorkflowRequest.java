package com.peoplecore.dto.request;


import lombok.Data;

import java.util.List;

@Data
public class ApprovalWorkflowRequest {

    private List<WorkflowLevelRequest> workflowLevels;
}
