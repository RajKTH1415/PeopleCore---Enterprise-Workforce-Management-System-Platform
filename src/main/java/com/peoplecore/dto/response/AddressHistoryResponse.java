package com.peoplecore.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressHistoryResponse {

    private Long historyId;

    private Long addressId;

    private Long employeeId;

    private String action;

    private Map<String, Object> oldValue;

    private Map<String, Object> newValue;

    private List<String> changedFields;

    private LocalDateTime changedAt;

    private String changedBy;

    private String ipAddress;

    private String remarks;
}
