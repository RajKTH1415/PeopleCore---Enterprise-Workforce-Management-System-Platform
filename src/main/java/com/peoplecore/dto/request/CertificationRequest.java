package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificationRequest {

    @NotBlank(message = "Certification name is required")
    private String name;

    @NotBlank(message = "Issuer is required")
    private String issuer;
}
