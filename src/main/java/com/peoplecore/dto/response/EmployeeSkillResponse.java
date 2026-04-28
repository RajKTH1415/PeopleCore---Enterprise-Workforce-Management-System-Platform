package com.peoplecore.dto.response;

import com.peoplecore.enums.ProficiencyLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillResponse {

    private Long employeeId;
    private Long skillId;
    private String skillName;
    private String employeeName;
    private String skillCategory;

    private Integer experienceYears;
    private ProficiencyLevel proficiencyLevel;
    private LocalDate lastUsed;

    private Boolean isVerified;
    private String source;
    private String notes;

    private Long verifiedBy;
    private LocalDateTime verifiedDate;
    private Long certificationVerifiedBy;
    private LocalDateTime certificationVerifiedDate;
    private String certificationNotes;

    private String certificationUrl;
    private String certificationVerificationStatus;

    private Boolean isDeleted;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updateDate;
    private String updatedBy;
}
