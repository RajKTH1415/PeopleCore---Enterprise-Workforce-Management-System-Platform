package com.peoplecore.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillVerificationResponse {

    private Long employeeId;
    private String employeeName;

    private Long skillId;
    private String skillName;
    private String skillCategory;

    private Boolean verified;
    private Long verifiedBy;
    private String verifierName;
    private LocalDateTime verifiedDate;

    private Integer experienceYears;
    private String proficiencyLevel;
    private LocalDate lastUsed;

    private String source;
    private String notes;

    private String certificationUrl;
    private String certificationVerificationStatus;
    private Long certificationVerifiedBy;
    private LocalDateTime certificationVerifiedDate;
    private String certificationNotes;
}