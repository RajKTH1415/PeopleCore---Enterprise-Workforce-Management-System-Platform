package com.peoplecore.controller;

import com.peoplecore.dto.request.SkillRequest;
import com.peoplecore.dto.request.UpdateSkillRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.SkillResponse;
import com.peoplecore.service.SkillService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillsController {

    private final SkillService skillService;

    public SkillsController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@RequestBody SkillRequest skillRequest , HttpServletRequest httpServletRequest){
        SkillResponse skillResponse = skillService.create(skillRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Skill created successfully", httpServletRequest.getRequestURI(), skillResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SkillResponse>>> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            HttpServletRequest httpServletRequest) {

        PageResponse<SkillResponse> response = skillService.getAllSkills(
                page,
                size,
                sort,
                direction,
                category,
                search);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skills fetched successfully",httpServletRequest.getRequestURI(), response));

    }
    /* This is only for testing purpose */
    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<Void>> deleteAllSkills(HttpServletRequest httpServletRequest) {
        skillService.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI(), "All skills deleted successfully", null));
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillById(@PathVariable("skillId") Long skillId , HttpServletRequest httpServletRequest){
        SkillResponse skillResponse = skillService.getSkillById(skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill fetched successfully", httpServletRequest.getRequestURI(), skillResponse));
    }
    @PutMapping("/{skillId}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(@PathVariable("skillId") Long skillId , @RequestBody UpdateSkillRequest updateSkillRequest , HttpServletRequest httpServletRequest){
        SkillResponse skillResponse = skillService.updateSkill(skillId, updateSkillRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill updated successfully", httpServletRequest.getRequestURI(), skillResponse));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<SkillResponse>> deleteSkillById(@PathVariable("skillId") Long skillId, HttpServletRequest httpServletRequest){
        SkillResponse skillResponse = skillService.deleteSkillById(skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Skill deleted successfully", httpServletRequest.getRequestURI(), skillResponse));
    }
    @PatchMapping("/{skillId}/restore")
    public ResponseEntity<ApiResponse<SkillResponse>> restoreSkill(@PathVariable("skillId") Long skillId , HttpServletRequest httpServletRequest){
        SkillResponse skillResponse = skillService.restore(skillId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), " Skill restore successfully", httpServletRequest.getRequestURI(),skillResponse));
    }
}
