package com.peoplecore.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class AssignCertificationRequest {

    private Long certificateId;
    private  String certificateNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String proofUrl;


}
