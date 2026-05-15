package com.peoplecore.dto.request;

import lombok.Data;

@Data
public class StateRequest {

    private Long countryId;

    private String code;

    private String name;

    private String capital;
}