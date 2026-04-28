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
public class SkillRecommendationResponse {

    private Long employeeId;
    private Integer totalRecommendations;
    private List<RecommendedSkill> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedSkill {
        private Long skillId;
        private String skillName;
        private String category;
        private String recommendationReason;
        private List<String> recommendedCourses;
        private List<String> recommendedCertifications;
        private Integer priority;
    }
}