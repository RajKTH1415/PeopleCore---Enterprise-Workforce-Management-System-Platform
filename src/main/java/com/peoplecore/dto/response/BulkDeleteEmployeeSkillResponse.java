package com.peoplecore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkDeleteEmployeeSkillResponse {

    private Long employeeId;
    private List<Long> deletedSkillIds;
    private Integer deletedCount;
}
