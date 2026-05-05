package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentVersionDto {
    private Integer version;
    private String fileName;
    private Long fileSize;
    private String storageKey;
    private String uploadedBy;
    private String versionComment;
    private LocalDateTime uploadedAt;
}