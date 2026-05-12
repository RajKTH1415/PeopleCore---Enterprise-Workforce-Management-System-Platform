package com.peoplecore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificationResponse {

    private Long id;
    private String name;
    private String issuer;

    private Boolean deleted;


    private LocalDateTime createdDate;
    private String createdBy;
    private Boolean isDeleted;

    private LocalDateTime updatedDate;
    private String updatedBy;
}