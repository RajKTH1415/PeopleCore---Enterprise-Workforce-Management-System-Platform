package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@Entity
@Table(name = "employee_certification_audit")
public class EmployeeCertificationAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long certificationId;

    private String action;
    // UPLOADED, VERIFIED, RENEWED, UPDATED

    private String fileName;

    private String fileType;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] fileData;

    private String performedBy;

    private LocalDateTime performedAt;

    private String remarks;
}
