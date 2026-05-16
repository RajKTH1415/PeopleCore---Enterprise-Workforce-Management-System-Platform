package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateAddressRequest {

    @NotBlank
    private String addressType;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    private String landmark;

    @NotNull
    private Long cityId;

    @NotNull
    private Long stateId;

    @NotNull
    private Long countryId;

    @NotBlank
    private String postalCode;

    private Boolean primaryAddress;

    private String residenceType;

    private LocalDate staySince;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String notes;
}