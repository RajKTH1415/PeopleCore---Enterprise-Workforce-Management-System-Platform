package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CertificationAuditResponse {

    private Long id;

    private Long employeeId;

    private Long certificationId;

    private String action;

    private String fileName;

    private String fileType;

    private String performedBy;

    private LocalDateTime performedAt;

    private String remarks;

    private String fileUrl;
}