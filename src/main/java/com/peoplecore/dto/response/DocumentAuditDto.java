package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentAuditDto {
    private String action;
    private String actionType;
    private String accessType;
    private String remarks;
    private String performedBy;
    private LocalDateTime performedAt;

//    private String oldValue;
//    private String newValue;

    private Object oldValue;
    private Object newValue;
}
