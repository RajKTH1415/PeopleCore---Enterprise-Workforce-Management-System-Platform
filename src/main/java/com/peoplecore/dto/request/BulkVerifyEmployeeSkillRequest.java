package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkVerifyEmployeeSkillRequest {

    @NotNull
    private Boolean verified;
    private Long skillId;

    @NotNull
    private Long verifiedBy;

    private String notes;
}
