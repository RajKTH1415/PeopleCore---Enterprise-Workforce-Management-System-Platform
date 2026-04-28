package com.peoplecore.dto.response;

import com.peoplecore.enums.CertificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BulkVerificationFinalResponse {

    private Summary summary;
    private Result result;

    @Getter
    @Setter
    @Builder
    public static class Summary {
        private Long employeeId;
        private int totalRequested;
        private int successCount;
        private int failedCount;
        private List<Long> failedSkillIds;
    }

    @Getter
    @Setter
    @Builder
    public static class Result {
        private Long employeeId;
        private List<Long> updatedSkills;
        private CertificationStatus status;
        private int updatedCount;
    }
}