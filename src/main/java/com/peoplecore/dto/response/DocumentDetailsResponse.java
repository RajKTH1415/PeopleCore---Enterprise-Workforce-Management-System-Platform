package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DocumentDetailsResponse {

    private String documentId;
    private Long employeeId;
    private String documentType;
    private String category;
    private String title;
    private String description;

    private String fileName;
    private String fileUrl;
    private Long fileSize;

    private Integer version;
    private String status;
    private String verificationStatus;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    private Boolean isPrimary;
    private List<String> tags;

    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;

    private List<DocumentVersionDto> versions;
    private List<DocumentAuditDto> audits;
}