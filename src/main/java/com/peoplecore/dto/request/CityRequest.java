package com.peoplecore.dto.request;

import lombok.Data;

@Data
public class CityRequest {

    private Long stateId;

    private String code;

    private String name;

    private String pinCode;
}
