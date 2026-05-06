package com.peoplecore.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeleteDocumentResponse {

    private String documentId;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
}