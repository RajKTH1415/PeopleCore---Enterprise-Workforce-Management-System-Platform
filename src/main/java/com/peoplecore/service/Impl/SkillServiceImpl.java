package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.SkillRequest;
import com.peoplecore.dto.request.UpdateSkillRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.SkillResponse;
import com.peoplecore.module.Skill;
import com.peoplecore.repository.SkillRepository;
import com.peoplecore.service.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {


    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public SkillResponse create(SkillRequest request) {

        if (request.getName() != null && request.getName().trim().isEmpty()){
            throw new RuntimeException("skill name cannot be empty");
        }
        Optional<Skill> existingSkill =
                skillRepository.findByNameAndIsDeletedFalse(request.getName());

        if (existingSkill.isPresent()) {
            throw new RuntimeException("Skill already exists with name: " + request.getName());
        }
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setDescription(request.getDescription());
        skill.setIsDeleted(false);
        skill.setCreatedDate(LocalDateTime.now());
        skill.setCreatedBy("SYSTEM");
        skill.setUpdatedDate(LocalDateTime.now());
        skill.setUpdatedBy("SYSTEM");

        Skill savedSkill = skillRepository.save(skill);

        return SkillResponse.builder()
                .id(savedSkill.getId())
                .name(savedSkill.getName())
                .category(savedSkill.getCategory())
                .description(savedSkill.getDescription())
                .deleted(savedSkill.getIsDeleted())
                .createdDate(savedSkill.getCreatedDate())
                .createdBy(savedSkill.getCreatedBy())
                .updatedDate(savedSkill.getUpdatedDate())
                .updatedBy(savedSkill.getUpdatedBy())
                .build();
    }
    @Override
    public void deleteAll() {
        skillRepository.deleteAll();
    }

    @Override
    public SkillResponse getSkillById(Long id) {
        if (id == null && id <=0){
            throw new RuntimeException("Invalid skill ID :"+ id);
        }

        Skill skill = skillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new RuntimeException("skill not found with ID :"+ id));

        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .deleted(skill.getIsDeleted())
                .createdDate(skill.getCreatedDate())
                .createdBy(skill.getCreatedBy())
                .updatedBy(skill.getUpdatedBy())
                .updatedDate(skill.getUpdatedDate())
                .build();
    }

    @Override
    public SkillResponse updateSkill(Long id, UpdateSkillRequest updateSkillRequest) {
        if (id == null && id <= 0){
            throw new RuntimeException("Invalid skill ID :"+ id);
        }
        if (updateSkillRequest.getName() == null || updateSkillRequest.getName().trim().isEmpty()){
            throw new RuntimeException("skill name cannot be empty");
        }

        Skill skill = skillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new RuntimeException("skill not found with ID :"+ id));

        Optional<Skill> existingSkill  = skillRepository.findByNameAndIsDeletedFalse(updateSkillRequest.getName());

        if (existingSkill.isPresent() && !existingSkill.get().getId().equals(id)) {
            throw new RuntimeException("Skill already exists with name: " + updateSkillRequest.getName());
        }

        skill.setName(updateSkillRequest.getName());
        skill.setCategory(updateSkillRequest.getCategory());
        skill.setDescription(updateSkillRequest.getDescription());
        skill.setUpdatedDate(LocalDateTime.now());
        skill.setUpdatedBy("SYSTEM");

        Skill savedSkill = skillRepository.save(skill);

        return SkillResponse.builder()
                .id(savedSkill.getId())
                .name(savedSkill.getName())
                .category(savedSkill.getCategory())
                .description(savedSkill.getDescription())
                .deleted(savedSkill.getIsDeleted())
                .createdDate(savedSkill.getCreatedDate())
                .createdBy(savedSkill.getCreatedBy())
                .updatedDate(savedSkill.getUpdatedDate())
                .updatedBy(savedSkill.getUpdatedBy())
                .build();
    }

    @Override
    public SkillResponse deleteSkillById(Long id) {
        if (id == null && id <= 0){
            throw new RuntimeException("Invalid skill ID :"+ id);
        }
        Skill skill = skillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new RuntimeException("Skill not found with ID :"+ id));

        skill.setIsDeleted(true);
        skill.setCreatedDate(LocalDateTime.now());
        skill.setCreatedBy("SYSTEM");

        Skill savedSkill  = skillRepository.save(skill);

        return SkillResponse.builder()
                .name(savedSkill.getName())
                .build();
    }

    @Override
    public SkillResponse restore(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid skill ID");
        }

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Skill not found with id: " + id));

        if (!Boolean.TRUE.equals(skill.getIsDeleted())) {
            throw new RuntimeException("Skill is already active");
        }
        skill.setIsDeleted(false);
        skill.setUpdatedDate(LocalDateTime.now());
        skill.setUpdatedBy("SYSTEM");
        skill.setUpdatedDate(LocalDateTime.now());

        Skill savedSkill = skillRepository.save(skill);

        return SkillResponse.builder()
                .name(savedSkill.getName())
                .category(savedSkill.getCategory())
                .description(savedSkill.getDescription())
                .deleted(savedSkill.getIsDeleted())
                .createdDate(savedSkill.getCreatedDate())
                .createdBy(savedSkill.getCreatedBy())
                .updatedBy(savedSkill.getUpdatedBy())
                .updatedDate(savedSkill.getUpdatedDate())
                .build();
    }

    // ===============================
// SkillServiceImpl.java
// ===============================
    @Override
    public PageResponse<SkillResponse> getAllSkills(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String category,
            String search) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Skill> skillPage;

        boolean hasCategory = category != null && !category.trim().isEmpty();
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasCategory && hasSearch) {
            skillPage = skillRepository
                    .findByCategoryIgnoreCaseAndNameContainingIgnoreCaseAndIsDeletedFalse(
                            category,
                            search,
                            pageable
                    );
        } else if (hasCategory) {
            skillPage = skillRepository
                    .findByCategoryIgnoreCaseAndIsDeletedFalse(
                            category,
                            pageable
                    );
        } else if (hasSearch) {
            skillPage = skillRepository
                    .findByNameContainingIgnoreCaseAndIsDeletedFalse(
                            search,
                            pageable
                    );
        } else {
            skillPage = skillRepository.findByIsDeletedFalse(pageable);
        }

        List<SkillResponse> responses = skillPage.getContent()
                .stream()
                .map(skill -> SkillResponse.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .category(skill.getCategory())
                        .description(skill.getDescription())
                        .deleted(skill.getIsDeleted())
                        .createdDate(skill.getCreatedDate())
                        .createdBy(skill.getCreatedBy())
                        .updatedDate(skill.getUpdatedDate())
                        .updatedBy(skill.getUpdatedBy())
                        .build())
                .toList();

        return PageResponse.<SkillResponse>builder()
                .content(responses)
                .page(skillPage.getNumber())
                .size(skillPage.getSize())
                .totalElements(skillPage.getTotalElements())
                .totalPages(skillPage.getTotalPages())
                .last(skillPage.isLast())
                .build();
    }
}
