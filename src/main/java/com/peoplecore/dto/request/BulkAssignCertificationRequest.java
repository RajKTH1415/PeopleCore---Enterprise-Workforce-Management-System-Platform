package com.peoplecore.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BulkAssignCertificationRequest {

    private List<Long> employeeIds;
    private Long certificationId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String certificateNumber;
    private String proofUrl;
}