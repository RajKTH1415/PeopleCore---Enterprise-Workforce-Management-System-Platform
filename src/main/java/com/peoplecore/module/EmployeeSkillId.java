package com.peoplecore.module;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeSkillId implements Serializable {

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "skill_id")
    private Long skillId;
}