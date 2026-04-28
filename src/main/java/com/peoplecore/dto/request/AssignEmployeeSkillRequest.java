package com.peoplecore.dto.request;
import com.peoplecore.enums.ProficiencyLevel;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignEmployeeSkillRequest {


    private Long skillId;


    private Integer experienceYears;


    private ProficiencyLevel proficiencyLevel;

    private LocalDate lastUsed;


    private String source;


    private String notes;

    private String certificationUrl;
}