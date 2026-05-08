package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentApprovalResponse {

    private Long approvalId;

    private String documentId;

    private String approvalStatus;

    private String approvedBy;

    private String rejectionReason;

    private LocalDateTime approvedAt;
}