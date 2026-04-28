package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeLifecycleHistoryResponse {

    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;
    private String changedBy;
    private String remarks;
}