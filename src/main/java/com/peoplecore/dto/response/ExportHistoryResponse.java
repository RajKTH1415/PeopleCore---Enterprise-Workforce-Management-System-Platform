package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportHistoryResponse {

    private Long auditId;

    private String action;

    private String fileName;

    private String format;

    private Long fileSize;

    private String downloadedBy;

    private String downloadedAt;

    private String downloadUrl;

    private String createdAt;
}
