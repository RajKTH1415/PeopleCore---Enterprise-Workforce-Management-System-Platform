package com.peoplecore.dto.request;
import lombok.Data;

@Data
public class ApprovalEscalationRequest {

    private Long escalatedTo;

    private String escalationReason;
}
