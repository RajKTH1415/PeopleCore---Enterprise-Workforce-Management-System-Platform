package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificationIssuerResponse {

    private Long id;

    private String name;

    private String description;

    private String website;

    private Boolean active;
}