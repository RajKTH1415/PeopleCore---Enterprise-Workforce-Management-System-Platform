package com.peoplecore.dto.response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalAuditLogResponse {

    private Long auditId;
    private Long approvalId;
    private String documentId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private String actionBy;
    private LocalDateTime actionAt;
    private String remarks;
}
