package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employee_certifications")
@Getter
@Setter
public class EmployeeCertification extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "proof_url")
    private String proofUrl;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}