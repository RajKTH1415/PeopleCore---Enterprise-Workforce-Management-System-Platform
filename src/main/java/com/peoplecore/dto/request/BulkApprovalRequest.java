package com.peoplecore.dto.request;
import lombok.Data;

import java.util.List;

@Data
public class BulkApprovalRequest {

    private List<Long> approvalIds;
}