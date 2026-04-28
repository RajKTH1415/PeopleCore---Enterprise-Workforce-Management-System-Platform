package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.ProficiencyLevel;
import com.peoplecore.service.EmployeeSkillService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeSkillController {

    private final EmployeeSkillService employeeSkillService;

    @PostMapping("/{employeeId}/skills")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> assignSkill(
            @PathVariable Long employeeId,
            @RequestBody AssignEmployeeSkillRequest request, HttpServletRequest httpServletRequest) {

        EmployeeSkillResponse response = employeeSkillService.assignSkillToEmployee(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Skill assigned successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/{employeeId}/skills/bulk")
    public ResponseEntity<ApiResponse<List<EmployeeSkillResponse>>> bulkAssignSkills(
            @PathVariable Long employeeId,
            @RequestBody BulkAssignSkillRequest request,
            HttpServletRequest httpServletRequest) {

        List<EmployeeSkillResponse> responses =
                employeeSkillService.bulkAssignSkills(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Skills assigned successfully", httpServletRequest.getRequestURI(), responses));
    }

    @PutMapping("/{employeeId}/skills/{skillId}/verify")
    public ResponseEntity<ApiResponse<SkillVerificationResponse>> verifyEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            @RequestBody VerifyEmployeeSkillRequest request,
            HttpServletRequest httpServletRequest) {

        SkillVerificationResponse response = employeeSkillService.verifyEmployeeSkill(employeeId, skillId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill verified successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> getEmployeeSkill(
            @PathVariable("employeeId") Long empId,
            @PathVariable("skillId") Long skillId, HttpServletRequest httpServletRequest){

        EmployeeSkillResponse skillResponse = employeeSkillService.getEmployeeSkill(empId, skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skill fetched successfully", httpServletRequest.getRequestURI(), skillResponse));
    }

    @DeleteMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> deleteEmployeeSkill(
            @PathVariable("employeeId") Long  empId ,
            @PathVariable("skillId") Long skillId, HttpServletRequest httpServletRequest){
        EmployeeSkillResponse response = employeeSkillService.removeSkillFromEmployee(empId, skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill removed successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{skillId}/employees")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeSkillResponse>>> getEmployeesBySkill(
            @PathVariable Long skillId,
            @RequestParam(required = false) String proficiencyLevel,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 10, sort = "createdDate",
                    direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeSkillResponse> response =
                employeeSkillService.getEmployeesBySkill(
                        skillId,
                        proficiencyLevel,
                        verified,
                        category,
                        pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employees fetched successfully",httpServletRequest.getRequestURI(), response));
    }
    @PutMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> updateEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            @RequestBody UpdateEmployeeSkillRequest request,
            HttpServletRequest httpServletRequest) {

        EmployeeSkillResponse response =
                employeeSkillService.updateEmployeeSkill(
                        employeeId,
                        skillId,
                        request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skill updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeSkillResponse>>> getEmployeeSkills(
            @PathVariable Long employeeId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "skill.name,asc") String[] sort,
            @RequestParam(required = false) ProficiencyLevel proficiency,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String search,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeSkillResponse> response =
                employeeSkillService.getEmployeeSkills(
                        employeeId,
                        page,
                        size,
                        sort,
                        proficiency,
                        verified,
                        search
                );
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skills fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/skills/{skillId}/restore")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> restoreEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            HttpServletRequest httpServletRequest) {

        EmployeeSkillResponse response =
                employeeSkillService.restoreEmployeeSkill(employeeId, skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Employee skill restored successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/skills/restore")
    public ResponseEntity<ApiResponse<List<EmployeeSkillResponse>>> bulkRestoreEmployeeSkills(
            @PathVariable Long employeeId,
            @RequestBody List<Long> skillIds,
            HttpServletRequest httpServletRequest) {

        List<EmployeeSkillResponse> responses =
                employeeSkillService.bulkRestoreEmployeeSkills(employeeId, skillIds);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Employee skills restored successfully", httpServletRequest.getRequestURI(), responses));

    }

    @GetMapping("/{employeeId}/skills/deleted")
    public ResponseEntity<ApiResponse<List<EmployeeSkillResponse>>> getDeletedEmployeeSkills(
            @PathVariable Long employeeId,
            HttpServletRequest httpServletRequest) {

        List<EmployeeSkillResponse> response =
                employeeSkillService.getDeletedEmployeeSkills(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Deleted employee skills fetched successfully",httpServletRequest.getRequestURI(), response));
    }
    @DeleteMapping("/{employeeId}/skills/{skillId}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            HttpServletRequest httpServletRequest) {

        employeeSkillService.permanentlyDeleteEmployeeSkill(
                employeeId,
                skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skill permanently deleted successfully",httpServletRequest.getRequestURI(), null));
    }

    @DeleteMapping("/{employeeId}/skills")
    public ResponseEntity<ApiResponse<BulkDeleteEmployeeSkillResponse>> bulkDeleteEmployeeSkills(
            @PathVariable Long employeeId,
            @RequestBody List<Long> skillIds,
            HttpServletRequest httpServletRequest) {

        BulkDeleteEmployeeSkillResponse response =
                employeeSkillService.bulkDeleteEmployeeSkills(employeeId, skillIds);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skills deleted successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/skills/verify/bulk")
    public ResponseEntity<ApiResponse<BulkVerificationFinalResponse>> bulkVerifyEmployeeSkills(
            @PathVariable Long employeeId,
            @RequestBody List<BulkVerifyEmployeeSkillRequest> requests,
            HttpServletRequest httpServletRequest) {

        BulkVerificationFinalResponse response =
                employeeSkillService.bulkVerifyEmployeeSkills(employeeId, requests);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee skills verified successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills/summary")
    public ResponseEntity<ApiResponse<SkillSummaryResponse>> getSkillSummary(
            @PathVariable Long employeeId,
            HttpServletRequest httpServletRequest) {

        SkillSummaryResponse response =
                employeeSkillService.getEmployeeSkillSummary(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill summary fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills/analytics")
    public ResponseEntity<ApiResponse<SkillAnalyticsResponse>> getSkillAnalytics(
            @PathVariable Long employeeId,
            HttpServletRequest httpServletRequest) {

        SkillAnalyticsResponse response =
                employeeSkillService.getEmployeeSkillAnalytics(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill analytics fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills/gap-analysis")
    public ResponseEntity<ApiResponse<SkillGapAnalysisResponse>> getSkillGapAnalysis(
            @PathVariable Long employeeId,
            HttpServletRequest httpServletRequest) {

        SkillGapAnalysisResponse response =
                employeeSkillService.getSkillGapAnalysis(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill gap analysis fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/skills/recommendations")
    public ResponseEntity<ApiResponse<SkillRecommendationResponse>> getSkillRecommendations(
            @PathVariable Long employeeId,
            HttpServletRequest httpServletRequest) {

        SkillRecommendationResponse response =
                employeeSkillService.getSkillRecommendations(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill recommendations fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/search-by-skills")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeSkillResponse>>> searchEmployeesBySkills(
            @RequestBody SearchEmployeesBySkillsRequest request,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeSkillResponse> response =
                employeeSkillService.searchEmployeesBySkills(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employees fetched successfully", httpServletRequest.getRequestURI(), response));
    }
}