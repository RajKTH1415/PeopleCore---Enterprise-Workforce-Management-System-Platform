package com.peoplecore.dto.request;


import com.peoplecore.enums.CertificationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkUpdateCertificationRequest {

    private Long id;

    private String name;

    private String issuer;

    private CertificationStatus status;
}