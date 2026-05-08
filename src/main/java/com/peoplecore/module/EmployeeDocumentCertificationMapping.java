package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_document_certification_mapping")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentCertificationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "employee_certification_id", nullable = false)
    private Long employeeCertificationId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private String createdBy;
}