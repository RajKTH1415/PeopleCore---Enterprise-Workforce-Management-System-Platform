package com.peoplecore.module;

import com.peoplecore.enums.CertificationStatus;
import com.peoplecore.enums.ProficiencyLevel;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkill extends Auditable implements Serializable {

    @EmbeddedId
    private EmployeeSkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "employee_id",
            insertable = false,
            updatable = false
    )
    private Employee employee;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "skill_id",
            insertable = false,
            updatable = false
    )
    private Skill skill;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private ProficiencyLevel proficiencyLevel;

    @Column(name = "last_used")
    private LocalDate lastUsed;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "source")
    private String source;

    @Column(name = "notes")
    private String notes;

    @Column(name = "certification_url")
    private String certificationUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "certification_verification_status")
    private CertificationStatus certificationVerificationStatus;

    @Column(name = "certification_verified_by")
    private Long certificationVerifiedBy;

    @Column(name = "certification_notes")
    private String certificationNotes;

    @Column(name = "certification_verified_date")
    private LocalDateTime certificationVerifiedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}