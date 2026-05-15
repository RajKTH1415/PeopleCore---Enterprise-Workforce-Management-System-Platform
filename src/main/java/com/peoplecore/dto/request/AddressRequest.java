package com.peoplecore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    private String addressType;  // PERMANENT, CURRENT, OFFICE, EMERGENCY, COMMUNICATION, TEMPORARY

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private Long cityId;

    private String state;

    private Long stateId;

    private String pincode;

    private String country;

    private Long countryId;

    private Double latitude;

    private Double longitude;

    private String googlePlaceId;

    private Boolean isPrimary = false;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String residenceType;  // OWNED, RENTED, LEASED, COMPANY_PROVIDED, OTHER

    private LocalDate staySince;

    private String notes;
}
