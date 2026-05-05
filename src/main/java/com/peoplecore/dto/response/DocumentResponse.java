package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DocumentResponse {

    private String documentId;
    private Long employeeId;
    private String documentType;
    private String category;
    private String title;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Integer version;
    private LocalDate issueDate;
    private Boolean isPrimary;
    private LocalDate expiryDate;
    private String status;
    private String verificationStatus;
    private List<String> tags;

    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}