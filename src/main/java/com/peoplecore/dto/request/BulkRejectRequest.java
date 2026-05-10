package com.peoplecore.dto.request;
import lombok.Data;

import java.util.List;

@Data
public class BulkRejectRequest {

    private List<Long> approvalIds;

    private String rejectionReason;
}