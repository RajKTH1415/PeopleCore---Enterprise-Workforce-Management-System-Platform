package com.peoplecore.module;

import com.peoplecore.constants.CertificationAuditActions;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Builder
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

//    @Enumerated(EnumType.STRING)
//    private CertificationAuditActions action;

    private String fileName;

    private String fileType;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] fileData;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    private String performedBy;

    private LocalDateTime performedAt;

    private String remarks;
}
