package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String documentId;
    private String documentType;
    private String documentCategory;
    private String title;
    private String description;

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String fileHash;

    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    private Boolean isPrimary;
    private String[] tags;

    private LocalDateTime uploadedAt;

    private String createdBy;
}