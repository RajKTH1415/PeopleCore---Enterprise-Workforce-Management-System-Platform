package com.peoplecore.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CertificationIssuerRequest {

    @NotBlank(message = "Issuer name is required")
    private String name;

    private String description;

    private String website;
}
