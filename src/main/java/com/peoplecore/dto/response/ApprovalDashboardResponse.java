package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalDashboardResponse {

    private long pending;
    private long approved;
    private long rejected;
    private long cancelled;
}
