package com.peoplecore.service;

import com.peoplecore.dto.request.SkillRequest;
import com.peoplecore.dto.request.UpdateSkillRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.SkillResponse;

public interface SkillService {

    SkillResponse create(SkillRequest request);

    void deleteAll();

     SkillResponse getSkillById(Long id);

     SkillResponse updateSkill(Long id , UpdateSkillRequest updateSkillRequest);

     SkillResponse deleteSkillById(Long id);

    SkillResponse restore(Long id);

    PageResponse<SkillResponse> getAllSkills(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String category,
            String search
    );
}
