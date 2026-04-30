package com.peoplecore.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationVerificationResponse {

    private Long employeeId;
    private Long certificationId;
    private String certificationName;
    private String certificationNumber;

    private String verificationStatus;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String verificationNotes;

    private Boolean verified;
}

