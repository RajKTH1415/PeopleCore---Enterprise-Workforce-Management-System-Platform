package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BulkAssignResponse {

    private int totalRequested;
    private int successCount;
    private int failedCount;
    private List<Long> successEmployeeIds;
    private List<String> failedRecords; // "empId - reason"
}
