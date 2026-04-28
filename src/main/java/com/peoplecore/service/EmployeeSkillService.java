package com.peoplecore.service;

import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.ProficiencyLevel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeSkillService {

    EmployeeSkillResponse assignSkillToEmployee(
            Long employeeId,
            AssignEmployeeSkillRequest request
    );

    SkillVerificationResponse verifyEmployeeSkill(
            Long employeeId,
            Long skillId,
            VerifyEmployeeSkillRequest request
    );

    EmployeeSkillResponse getEmployeeSkill(Long empId , Long skillId);

    EmployeeSkillResponse removeSkillFromEmployee(Long employeeId, Long skillId);

    PageResponse<EmployeeSkillResponse> getEmployeesBySkill(
            Long skillId,
            String proficiencyLevel,
            Boolean verified,
            String category,
            Pageable pageable
    );
    EmployeeSkillResponse updateEmployeeSkill(
            Long employeeId,
            Long skillId,
            UpdateEmployeeSkillRequest request
    );

    List<EmployeeSkillResponse> bulkAssignSkills(
            Long employeeId,
            BulkAssignSkillRequest request
    );

    PageResponse<EmployeeSkillResponse> getEmployeeSkills(
            Long empId,
            int page,
            int size,
            String[] sort,
            ProficiencyLevel proficiencyLevel,
            Boolean verified,
            String search
    );

    EmployeeSkillResponse restoreEmployeeSkill(Long employeeId, Long skillId);


    List<EmployeeSkillResponse> bulkRestoreEmployeeSkills(
            Long employeeId,
            List<Long> skillIds
    );

    List<EmployeeSkillResponse> getDeletedEmployeeSkills(Long employeeId);

    void permanentlyDeleteEmployeeSkill(Long employeeId, Long skillId);

    BulkDeleteEmployeeSkillResponse bulkDeleteEmployeeSkills(
            Long employeeId,
            List<Long> skillIds
    );

    BulkVerificationFinalResponse bulkVerifyEmployeeSkills(
            Long employeeId,
            List<BulkVerifyEmployeeSkillRequest> requests
    );

    SkillSummaryResponse getEmployeeSkillSummary(Long id);
    SkillAnalyticsResponse getEmployeeSkillAnalytics(Long employeeId);

    SkillGapAnalysisResponse getSkillGapAnalysis(Long employeeId);

    SkillRecommendationResponse getSkillRecommendations(Long employeeId);

    PageResponse<EmployeeSkillResponse> searchEmployeesBySkills(
            SearchEmployeesBySkillsRequest request
    );
}
