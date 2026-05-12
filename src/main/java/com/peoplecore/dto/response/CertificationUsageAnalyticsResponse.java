package com.peoplecore.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificationUsageAnalyticsResponse {

    private Long totalCertifications;

    private Long activeCount;
    private Long inactiveCount;
    private Long deprecatedCount;

    private String mostPopularIssuer;
    private String mostAssignedCertification;
}