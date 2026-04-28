package com.peoplecore.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAssignSkillRequest {

    private List<AssignEmployeeSkillRequest> skills;
}