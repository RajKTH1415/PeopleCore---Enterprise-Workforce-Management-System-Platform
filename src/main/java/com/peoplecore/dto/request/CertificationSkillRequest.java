package com.peoplecore.dto.request;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CertificationSkillRequest {
    private Set<Long> skillIds;
}