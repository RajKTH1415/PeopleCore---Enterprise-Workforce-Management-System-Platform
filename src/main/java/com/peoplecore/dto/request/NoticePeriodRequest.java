package com.peoplecore.dto.request;

import lombok.Data;

@Data
public class NoticePeriodRequest {

    private Integer noticeDays;   // e.g. 30, 60
    private String reason;        // Resigned / Personal / etc.
}