package com.peoplecore.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapAnalysisResponse {

    private Long employeeId;
    private Integer totalAssignedSkills;
    private Integer totalMissingSkills;
    private Double skillCoveragePercentage;

    private List<String> existingSkills;
    private List<String> missingSkills;

    private List<String> recommendedSkills;
}