package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateResponse {

    private Long id;

    private Long countryId;

    private String countryName;

    private String code;

    private String name;

    private String capital;

    private Boolean isActive;
}