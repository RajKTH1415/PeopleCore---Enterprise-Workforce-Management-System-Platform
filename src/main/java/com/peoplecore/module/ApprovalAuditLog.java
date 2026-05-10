package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "approval_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "action")
    private String action;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "action_by")
    private Long actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @Column(name = "remarks")
    private String remarks;
}