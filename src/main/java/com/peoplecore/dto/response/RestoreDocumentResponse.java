package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RestoreDocumentResponse {

    private String documentId;
    private Boolean restored;
    private LocalDateTime restoredAt;
    private String restoredBy;
}
