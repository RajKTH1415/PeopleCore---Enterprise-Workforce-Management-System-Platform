package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentAccessLogResponse {

    private Long id;

    private Long documentRefId;

    private String documentId;

    private String accessedBy;

    private String accessType;

    private LocalDateTime accessedAt;

    private String ipAddress;

    private String userAgent;
}
