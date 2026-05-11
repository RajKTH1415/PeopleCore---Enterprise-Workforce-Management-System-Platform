package com.peoplecore.dto.response;

import com.peoplecore.enums.ActionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeDocumentAuditResponse {

    private Long id;

    private Long documentId;

    private Long employeeId;

    private String action;

    private String fileName;

    private String fileUrl;

    private String remarks;

    private String performedBy;

    private LocalDateTime performedAt;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String status;
}
