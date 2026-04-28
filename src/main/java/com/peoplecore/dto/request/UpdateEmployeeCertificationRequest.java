package com.peoplecore.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeCertificationRequest {

    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String proofUrl;
    private String certificateNumber;
}
