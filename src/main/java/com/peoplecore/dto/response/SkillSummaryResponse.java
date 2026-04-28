package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class SkillSummaryResponse {

    private Long employeeId;

    private int totalSkills;

    private int verifiedSkills;

    private int pendingSkills;

    private int expertSkills;

    private int expiredCertifications;
}