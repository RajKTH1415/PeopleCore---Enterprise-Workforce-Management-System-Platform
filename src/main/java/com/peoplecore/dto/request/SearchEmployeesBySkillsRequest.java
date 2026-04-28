package com.peoplecore.dto.request;
import com.peoplecore.enums.ProficiencyLevel;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEmployeesBySkillsRequest {

    private List<Long> skillIds;

    @Builder.Default
    private Boolean matchAll = false;

    private ProficiencyLevel minimumProficiency;
}
