package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    private String status;

    @Column(name = "proof_url")
    private String proofUrl;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    private String verifiedBy;

    private LocalDate verifiedDate;

    @Column(length = 1000)
    private String verificationNotes;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "certificate_file")
    private byte[] certificateFile;

    private String fileName;

    private String fileType;
}