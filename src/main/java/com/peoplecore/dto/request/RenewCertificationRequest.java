package com.peoplecore.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RenewCertificationRequest {

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private String certificateNumber;

    private String proofUrl;
}