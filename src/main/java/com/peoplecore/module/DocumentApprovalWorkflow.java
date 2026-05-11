package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_approval_workflows")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentApprovalWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "workflow_status")
    private String workflowStatus;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "action_at")
    private LocalDateTime actionAt;
}