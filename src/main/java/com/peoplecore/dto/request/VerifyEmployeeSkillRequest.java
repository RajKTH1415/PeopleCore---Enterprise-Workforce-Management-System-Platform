package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyEmployeeSkillRequest {

    @NotNull
    private Boolean verified;

    @NotNull
    private Long verifiedBy;

    private String notes;

}