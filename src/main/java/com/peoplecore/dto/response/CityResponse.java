package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityResponse {

    private Long id;

    private Long stateId;

    private String stateName;

    private String code;

    private String name;

    private String pinCode;

    private Boolean isActive;
}
