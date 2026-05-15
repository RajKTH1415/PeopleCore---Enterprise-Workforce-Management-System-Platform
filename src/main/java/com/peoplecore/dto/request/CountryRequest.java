package com.peoplecore.dto.request;


import lombok.Data;

@Data
public class CountryRequest {

    private String code;

    private String name;

    private String dialCode;

    private String currencyCode;
}