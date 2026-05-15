package com.peoplecore.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CountryResponse {


    private String code;

    private String name;

    private String dialCode;

    private String currencyCode;

    private Boolean isActive;

    private LocalDateTime createdDate;

    private String createdBy;

    private LocalDateTime updatedDate;

    private String updatedBy;
}