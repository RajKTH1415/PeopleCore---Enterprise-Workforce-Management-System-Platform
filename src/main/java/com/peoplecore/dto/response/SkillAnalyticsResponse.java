package com.peoplecore.dto.response;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillAnalyticsResponse {

    private Long employeeId;

    private Integer totalSkills;
    private Integer verifiedSkills;
    private Integer pendingSkills;

    private Integer expertSkills;
    private Integer intermediateSkills;
    private Integer beginnerSkills;

    private Double averageExperience;
}