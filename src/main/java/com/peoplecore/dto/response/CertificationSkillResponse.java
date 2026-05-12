package com.peoplecore.dto.response;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificationSkillResponse {

    private Long certificationId;
    private String certificationName;

    private Set<String> skills;
}