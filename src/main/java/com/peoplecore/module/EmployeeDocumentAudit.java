package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_document_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → employee_documents.id
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private String action;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    private String remarks;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "access_type")
    private String accessType;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;


    @Column(name = "user_agent")
    private String userAgent;

    private String status;

    @Column(name = "old_value", columnDefinition = "jsonb")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "jsonb")
    private String newValue;
}
