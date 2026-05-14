package com.peoplecore.dto.response;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmployeeCertificationHistoryResponse {

    private Long id;

    private Long employeeId;

    private Long certificationId;

    private String action;
    // ASSIGNED, VERIFIED, REJECTED, RENEWED, UPDATED, REMOVED

    // 🔥 JSON STRUCTURE (IMPORTANT UPGRADE)

    private Map<String, Object> previousValue;

    private Map<String, Object> newValue;

    private String changedBy;

    private LocalDateTime changedAt;

    private String remarks;

    // getters & setters
}