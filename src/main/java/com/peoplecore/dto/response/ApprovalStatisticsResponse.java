package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalStatisticsResponse {

    private long totalApprovals;

    private long approvedCount;

    private long rejectedCount;

    private long pendingCount;

    private long cancelledCount;

    private double approvalRate;

    private double rejectionRate;

    private double averageApprovalTimeInHours;
}
