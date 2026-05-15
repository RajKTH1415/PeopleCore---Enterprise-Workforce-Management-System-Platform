package com.peoplecore.dto.response;//package com.peoplecore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;

    private String addressType;
    private String addressTypeDisplay;

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

    private String fullAddress;
    private String formattedAddress;

    private Double latitude;
    private Double longitude;
    private String googlePlaceId;

    private Boolean isVerified;
    private String verificationStatus;
    private LocalDateTime verifiedDate;
    private String verifiedByName;

    private Boolean isPrimary;
    private Boolean isActive;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String residenceType;
    private LocalDate staySince;
    private Integer stayDurationMonths;
    private String notes;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;

}