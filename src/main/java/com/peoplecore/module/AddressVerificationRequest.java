package com.peoplecore.module;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "address_verification_request",
        indexes = {
                @Index(name = "idx_address_verification_address", columnList = "address_id"),
                @Index(name = "idx_address_verification_assigned", columnList = "assigned_to"),
                @Index(name = "idx_address_verification_employee", columnList = "employee_id"),
                @Index(name = "idx_address_verification_requested_date", columnList = "requested_date"),
                @Index(name = "idx_address_verification_status", columnList = "verification_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressVerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================
    // Address Mapping
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "address_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_address_verification_address")
    )
    private EmployeeAddress address;

    // =========================================
    // Employee Mapping
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_address_verification_employee")
    )
    private Employee employee;

    // =========================================
    // Verification Status
    // =========================================

    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = "PENDING";

    // =========================================
    // Request Details
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requested_by",
            foreignKey = @ForeignKey(name = "fk_address_verification_requested_by")
    )
    private Employee requestedBy;

    @Column(name = "requested_date", nullable = false, updatable = false)
    private LocalDateTime requestedDate;

    // =========================================
    // Assignment Details
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assigned_to",
            foreignKey = @ForeignKey(name = "fk_address_verification_assigned_to")
    )
    private Employee assignedTo;

    // =========================================
    // Verification Information
    // =========================================

    @Column(name = "verification_method", length = 30)
    private String verificationMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "verification_document_id",
            foreignKey = @ForeignKey(name = "fk_address_verification_document")
    )
    private EmployeeDocument verificationDocument;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // =========================================
    // Completion Details
    // =========================================

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "completed_by",
            foreignKey = @ForeignKey(name = "fk_address_verification_completed_by")
    )
    private Employee completedBy;

    // =========================================
    // Audit Fields
    // =========================================

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // =========================================
    // Lifecycle Hooks
    // =========================================

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        this.createdDate = now;

        if (this.requestedDate == null) {
            this.requestedDate = now;
        }

        if (this.verificationStatus == null) {
            this.verificationStatus = "PENDING";
        }
    }
}