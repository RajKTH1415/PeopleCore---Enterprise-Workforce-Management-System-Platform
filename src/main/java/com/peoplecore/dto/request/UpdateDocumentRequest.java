package com.peoplecore.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDocumentRequest {

    private String title;
    private String description;
    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Boolean isPrimary;
}