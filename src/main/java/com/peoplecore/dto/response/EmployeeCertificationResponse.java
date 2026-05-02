package com.peoplecore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.peoplecore.enums.CertificationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCertificationResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String certificationNumber;
    private String certificationName;
    private Long fileSize;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private String proofUrl;
    private Boolean isDeleted;
    private LocalDateTime uploadedAt;

    private String fileUrl;
    private String storagePath;

    private String verifiedBy;
    private LocalDate verifiedDate;
    private String verificationNotes;

    private String fileName;
    private String fileType;
    private Boolean certificateUploaded;
}
