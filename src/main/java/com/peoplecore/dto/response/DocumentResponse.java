package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
    private String status;
    private String verificationStatus;
    private LocalDateTime uploadedAt;
}