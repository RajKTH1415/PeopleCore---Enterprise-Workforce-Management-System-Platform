package com.peoplecore.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCertificationAuditResponse {

    private Long id;
    private Long employeeId;
    private Long certificationId;
    private String action;
    private String fileName;
    private String fileType;
    private String performedBy;
    private LocalDateTime performedAt;
    private String remarks;
}