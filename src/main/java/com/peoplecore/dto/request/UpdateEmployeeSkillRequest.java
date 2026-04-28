package com.peoplecore.dto.request;

import com.peoplecore.enums.ProficiencyLevel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeSkillRequest {

    private Integer experienceYears;

    private ProficiencyLevel proficiencyLevel;

    private LocalDate lastUsed;

    private String notes;

    private String certificationUrl;

    private String source;
}
