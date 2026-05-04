package com.peoplecore.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class EmployeeDashboardResponse {

    private Long totalEmployees;
    private Long active;
    private Long onProbation;
    private Long onNotice;
    private Long terminated;
    private Long expiringCertifications;
}
