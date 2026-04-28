package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.CertificationStatus;
import com.peoplecore.enums.ProficiencyLevel;
import com.peoplecore.enums.SkillSource;
import com.peoplecore.module.*;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import com.peoplecore.repository.EmployeeRepository;
import com.peoplecore.repository.EmployeeSkillRepository;
import com.peoplecore.repository.SkillRepository;
import com.peoplecore.service.EmployeeSkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;

    public EmployeeSkillServiceImpl(EmployeeSkillRepository employeeSkillRepository, EmployeeRepository employeeRepository, SkillRepository skillRepository, EmployeeCertificationsRepository employeeCertificationsRepository) {
        this.employeeSkillRepository = employeeSkillRepository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
    }

    @Override
    @Transactional
    public EmployeeSkillResponse assignSkillToEmployee(
            Long employeeId,
            AssignEmployeeSkillRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Skill not found with ID: " + request.getSkillId()));

        if (employeeSkillRepository
                .existsByEmployee_IdAndSkill_IdAndIsDeletedFalse(
                        employeeId,
                        request.getSkillId())) {
            throw new RuntimeException(
                    "Skill already assigned to employee");
        }

        EmployeeSkill employeeSkill = EmployeeSkill.builder()
                .id(new EmployeeSkillId(
                        employeeId,
                        request.getSkillId()))
                .employee(employee)
                .skill(skill)
                .experienceYears(request.getExperienceYears())
                .proficiencyLevel(request.getProficiencyLevel())
                .lastUsed(request.getLastUsed())

                .isVerified(false)
                .verifiedBy(null)

                .source(request.getSource() != null ? request.getSource() : SkillSource.SELF_DECLARED.name())
                .notes(request.getNotes())

                .isVerified(false)
                .source(request.getSource())
                .notes(request.getNotes())
                .certificationUrl(request.getCertificationUrl())
                .certificationVerificationStatus(CertificationStatus.PENDING)
                .certificationVerifiedBy(null)
                .certificationVerifiedDate(null)
                .certificationNotes(null)
                .isDeleted(false)
                .build();

        EmployeeSkill savedEmployeeSkill =
                employeeSkillRepository.save(employeeSkill);

        return EmployeeSkillResponse.builder()
                .employeeId(savedEmployeeSkill.getEmployee().getId())
                .skillId(savedEmployeeSkill.getSkill().getId())
                .skillName(savedEmployeeSkill.getSkill().getName())
                .skillCategory(savedEmployeeSkill.getSkill().getCategory())
                .experienceYears(savedEmployeeSkill.getExperienceYears())
                .proficiencyLevel(savedEmployeeSkill.getProficiencyLevel())
                .lastUsed(savedEmployeeSkill.getLastUsed())
                .isVerified(savedEmployeeSkill.getIsVerified())
                .source(savedEmployeeSkill.getSource())
                .notes(savedEmployeeSkill.getNotes())
                .certificationUrl(savedEmployeeSkill.getCertificationUrl())
                .certificationVerificationStatus(
                        savedEmployeeSkill.getCertificationVerificationStatus() != null
                                ? savedEmployeeSkill.getCertificationVerificationStatus().name()
                                : null
                )
                .createdDate(savedEmployeeSkill.getCreatedDate())
                .createdBy(savedEmployeeSkill.getCreatedBy())
                .build();
    }

    @Override
    public SkillVerificationResponse verifyEmployeeSkill(Long employeeId, Long skillId, VerifyEmployeeSkillRequest request) {
        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployee_IdAndSkill_IdAndIsDeletedFalse(
                        employeeId, skillId)
                .orElseThrow(() -> new RuntimeException(
                        "Employee skill not found"));

        Employee verifier = employeeRepository.findById(request.getVerifiedBy())
                .orElseThrow(() -> new RuntimeException(
                        "Verifier not found with ID: "
                                + request.getVerifiedBy()));

        employeeSkill.setIsVerified(request.getVerified());
        employeeSkill.setVerifiedBy(verifier.getId());
        employeeSkill.setVerifiedDate(LocalDateTime.now());
        employeeSkill.setCertificationVerifiedDate(LocalDateTime.now());
        employeeSkill.setCertificationVerificationStatus(
                request.getVerified()
                        ? CertificationStatus.VERIFIED
                        : CertificationStatus.REJECTED
        );

        employeeSkill.setCertificationVerifiedBy(
                verifier.getId());

        employeeSkill.setCertificationVerifiedDate(
                LocalDateTime.now());

        employeeSkill.setCertificationNotes(
                request.getNotes());

        if (request.getNotes() != null &&
                !request.getNotes().trim().isEmpty()) {
            employeeSkill.setNotes(request.getNotes());
        }

        EmployeeSkill saved = employeeSkillRepository.save(employeeSkill);

        return SkillVerificationResponse.builder()
                .employeeId(saved.getEmployee().getId())
                .employeeName(
                        saved.getEmployee().getFirstName() + " "
                                + saved.getEmployee().getLastName()
                )

                .skillId(saved.getSkill().getId())
                .skillName(saved.getSkill().getName())
                .skillCategory(saved.getSkill().getCategory())

                .verified(saved.getIsVerified())
                .verifiedBy(saved.getVerifiedBy())
                .verifierName(
                        verifier.getFirstName() + " "
                                + verifier.getLastName()
                )
                .verifiedDate(saved.getVerifiedDate())

                .experienceYears(saved.getExperienceYears())
                .proficiencyLevel(
                        saved.getProficiencyLevel() != null
                                ? saved.getProficiencyLevel().name()
                                : null
                )
                .lastUsed(saved.getLastUsed())

                .source(saved.getSource())
                .notes(saved.getNotes())

                .certificationUrl(saved.getCertificationUrl())
                .certificationVerificationStatus(
                        saved.getCertificationVerificationStatus().name()
                )
                .certificationVerifiedBy(
                        saved.getCertificationVerifiedBy()
                )
                .certificationVerifiedDate(
                        saved.getCertificationVerifiedDate()
                )
                .certificationNotes(
                        saved.getCertificationNotes()
                )
                .build();
    }

    @Override
    @Transactional
    public EmployeeSkillResponse getEmployeeSkill(Long empId, Long skillId) {

        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployee_IdAndSkill_IdAndIsDeletedFalse(empId, skillId)
                .orElseThrow(() ->
                        new RuntimeException("Employee skill not found"));

        return EmployeeSkillResponse.builder()
                .employeeId(employeeSkill.getEmployee().getId())
                .skillId(employeeSkill.getSkill().getId())
                .skillName(employeeSkill.getSkill().getName())
                .skillCategory(employeeSkill.getSkill().getCategory())

                .experienceYears(employeeSkill.getExperienceYears())
                .proficiencyLevel(employeeSkill.getProficiencyLevel())
                .lastUsed(employeeSkill.getLastUsed())

                .isVerified(employeeSkill.getIsVerified())
                .verifiedBy(employeeSkill.getVerifiedBy())
                .verifiedDate(employeeSkill.getVerifiedDate())

                .source(employeeSkill.getSource())
                .notes(employeeSkill.getNotes())

                .certificationUrl(employeeSkill.getCertificationUrl())

                .certificationVerificationStatus(
                        employeeSkill.getCertificationVerificationStatus() != null
                                ? employeeSkill.getCertificationVerificationStatus().name()
                                : null
                )

                .certificationVerifiedBy(employeeSkill.getCertificationVerifiedBy())
                .certificationVerifiedDate(employeeSkill.getCertificationVerifiedDate())
                .certificationNotes(employeeSkill.getCertificationNotes())

                .createdDate(employeeSkill.getCreatedDate())
                .createdBy(employeeSkill.getCreatedBy())
                .build();
    }

    @Override
    public EmployeeSkillResponse removeSkillFromEmployee(Long employeeId, Long skillId) {

        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployee_IdAndSkill_IdAndIsDeletedFalse(employeeId, skillId)
                .orElseThrow(() -> new RuntimeException("Employee skill not found"));
        employeeSkill.setIsDeleted(true);
        employeeSkill.setUpdatedDate(LocalDateTime.now());
        employeeSkill.setUpdatedBy("SYSTEM");
        EmployeeSkill savedEmployeeSkill = employeeSkillRepository.save(employeeSkill);

        return EmployeeSkillResponse.builder()
                .employeeId(savedEmployeeSkill.getEmployee().getId())
                .isDeleted(savedEmployeeSkill.getIsDeleted())
                .skillId(savedEmployeeSkill.getSkill().getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeSkillResponse> getEmployeesBySkill(
            Long skillId,
            String proficiencyLevel,
            Boolean verified,
            String category,
            Pageable pageable) {

        skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Skill not found with id: " + skillId));

        Page<EmployeeSkill> employeeSkillPage =
                employeeSkillRepository.findEmployeesBySkillWithFilters(
                        skillId,
                        ProficiencyLevel.valueOf(proficiencyLevel),
                        verified,
                        category,
                        pageable
                );

        List<EmployeeSkillResponse> responses = employeeSkillPage.getContent()
                .stream()
                .map(employeeSkill -> EmployeeSkillResponse.builder()
                        .employeeId(employeeSkill.getEmployee().getId())
                        .skillId(employeeSkill.getSkill().getId())
                        .skillName(employeeSkill.getSkill().getName())
                        .skillCategory(employeeSkill.getSkill().getCategory())

                        .experienceYears(employeeSkill.getExperienceYears())
                        .proficiencyLevel(employeeSkill.getProficiencyLevel())
                        .lastUsed(employeeSkill.getLastUsed())

                        .isVerified(employeeSkill.getIsVerified())
                        .verifiedBy(employeeSkill.getVerifiedBy())
                        .verifiedDate(employeeSkill.getVerifiedDate())

                        .source(employeeSkill.getSource())
                        .notes(employeeSkill.getNotes())

                        .certificationUrl(employeeSkill.getCertificationUrl())
                        .certificationVerificationStatus(
                                employeeSkill.getCertificationVerificationStatus() != null
                                        ? employeeSkill.getCertificationVerificationStatus().name()
                                        : null
                        )

                        .certificationVerifiedBy(
                                employeeSkill.getCertificationVerifiedBy()
                        )
                        .certificationVerifiedDate(
                                employeeSkill.getCertificationVerifiedDate()
                        )
                        .certificationNotes(
                                employeeSkill.getCertificationNotes()
                        )

                        .createdDate(employeeSkill.getCreatedDate())
                        .createdBy(employeeSkill.getCreatedBy())
                        .updateDate(employeeSkill.getUpdatedDate())
                        .updatedBy(employeeSkill.getUpdatedBy())
                        .build())
                .toList();

        return PageResponse.<EmployeeSkillResponse>builder()
                .content(responses)
                .page(employeeSkillPage.getNumber())
                .size(employeeSkillPage.getSize())
                .totalElements(employeeSkillPage.getTotalElements())
                .totalPages(employeeSkillPage.getTotalPages())
                .last(employeeSkillPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public EmployeeSkillResponse updateEmployeeSkill(
            Long employeeId,
            Long skillId,
            UpdateEmployeeSkillRequest request) {

        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployee_IdAndSkill_IdAndIsDeletedFalse(
                        employeeId,
                        skillId
                )
                .orElseThrow(() -> new RuntimeException(
                        "Employee skill not found with employeeId: "
                                + employeeId + " and skillId: " + skillId
                ));

        if (request.getExperienceYears() != null) {
            employeeSkill.setExperienceYears(request.getExperienceYears());
        }

        if (request.getProficiencyLevel() != null) {
            employeeSkill.setProficiencyLevel(
                    request.getProficiencyLevel()
            );
        }

        if (request.getLastUsed() != null) {
            employeeSkill.setLastUsed(request.getLastUsed());
        }

        if (request.getNotes() != null) {
            employeeSkill.setNotes(request.getNotes());
        }

        if (request.getCertificationUrl() != null) {
            employeeSkill.setCertificationUrl(
                    request.getCertificationUrl()
            );

            // Certification changed → reset verification status
            employeeSkill.setCertificationVerificationStatus(
                    CertificationStatus.PENDING
            );
            employeeSkill.setCertificationVerifiedBy(null);
            employeeSkill.setCertificationVerifiedDate(null);
            employeeSkill.setCertificationNotes(null);
        }

        if (request.getSource() != null) {
            employeeSkill.setSource(request.getSource());
        }

        EmployeeSkill updatedSkill =
                employeeSkillRepository.save(employeeSkill);

        return EmployeeSkillResponse.builder()
                .employeeId(updatedSkill.getEmployee().getId())
                .skillId(updatedSkill.getSkill().getId())
                .skillName(updatedSkill.getSkill().getName())
                .skillCategory(updatedSkill.getSkill().getCategory())
                .experienceYears(updatedSkill.getExperienceYears())
                .proficiencyLevel(updatedSkill.getProficiencyLevel())
                .lastUsed(updatedSkill.getLastUsed())
                .isVerified(updatedSkill.getIsVerified())
                .verifiedBy(updatedSkill.getVerifiedBy())
                .verifiedDate(updatedSkill.getVerifiedDate())
                .source(updatedSkill.getSource())
                .notes(updatedSkill.getNotes())
                .certificationUrl(updatedSkill.getCertificationUrl())
                .certificationVerificationStatus(
                        updatedSkill.getCertificationVerificationStatus() != null
                                ? updatedSkill.getCertificationVerificationStatus().name()
                                : null
                )
                .certificationVerifiedBy(
                        updatedSkill.getCertificationVerifiedBy()
                )
                .certificationVerifiedDate(
                        updatedSkill.getCertificationVerifiedDate()
                )
                .certificationNotes(
                        updatedSkill.getCertificationNotes()
                )
                .createdDate(updatedSkill.getCreatedDate())
                .createdBy(updatedSkill.getCreatedBy())
                .build();
    }

    @Override
    @Transactional
    public List<EmployeeSkillResponse> bulkAssignSkills(
            Long employeeId,
            BulkAssignSkillRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId));

        List<EmployeeSkillResponse> responses = new ArrayList<>();

        for (AssignEmployeeSkillRequest skillRequest : request.getSkills()) {

            Skill skill = skillRepository.findById(skillRequest.getSkillId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Skill not found with ID: " + skillRequest.getSkillId()));

            boolean alreadyExists = employeeSkillRepository
                    .existsByEmployee_IdAndSkill_IdAndIsDeletedFalse(
                            employeeId,
                            skillRequest.getSkillId()
                    );

            if (alreadyExists) {
                continue;
            }

            EmployeeSkill employeeSkill = EmployeeSkill.builder()
                    .id(new EmployeeSkillId(employeeId, skillRequest.getSkillId()))
                    .employee(employee)
                    .skill(skill)
                    .experienceYears(skillRequest.getExperienceYears())
                    .proficiencyLevel(skillRequest.getProficiencyLevel())
                    .lastUsed(skillRequest.getLastUsed())
                    .isVerified(false)
                    .source(skillRequest.getSource())
                    .notes(skillRequest.getNotes())
                    .certificationUrl(skillRequest.getCertificationUrl())
                    .certificationVerificationStatus(
                            CertificationStatus.PENDING
                    )
                    .isDeleted(false)
                    .build();

            EmployeeSkill saved =
                    employeeSkillRepository.save(employeeSkill);

            responses.add(
                    EmployeeSkillResponse.builder()
                            .employeeId(saved.getEmployee().getId())
                            .skillId(saved.getSkill().getId())
                            .skillName(saved.getSkill().getName())
                            .skillCategory(saved.getSkill().getCategory())
                            .experienceYears(saved.getExperienceYears())
                            .proficiencyLevel(saved.getProficiencyLevel())
                            .lastUsed(saved.getLastUsed())
                            .isVerified(saved.getIsVerified())
                            .source(saved.getSource())
                            .notes(saved.getNotes())
                            .certificationUrl(saved.getCertificationUrl())
                            .certificationVerificationStatus(
                                    saved.getCertificationVerificationStatus().name()
                            )
                            .createdDate(saved.getCreatedDate())
                            .createdBy(saved.getCreatedBy())
                            .build()
            );
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeSkillResponse> getEmployeeSkills(Long empId, int page, int size, String[] sort, ProficiencyLevel proficiencyLevel, Boolean verified, String search) {

        employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID :" + empId));

        Sort.Direction direction = sort.length > 1 ? Sort.Direction.fromString(sort[1]) : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<EmployeeSkill> employeeSkillPage =
                employeeSkillRepository.findEmployeeSkillsWithFilters(
                        empId,
                        proficiencyLevel,
                        verified,
                        search,
                        pageable
                );
        List<EmployeeSkillResponse> responses =
                employeeSkillPage.getContent()
                        .stream()
                        .map(employeeSkill -> EmployeeSkillResponse.builder()
                                .employeeId(employeeSkill.getEmployee().getId())
                                .skillId(employeeSkill.getSkill().getId())
                                .skillName(employeeSkill.getSkill().getName())
                                .skillCategory(employeeSkill.getSkill().getCategory())
                                .experienceYears(employeeSkill.getExperienceYears())
                                .proficiencyLevel(employeeSkill.getProficiencyLevel())
                                .lastUsed(employeeSkill.getLastUsed())
                                .isVerified(employeeSkill.getIsVerified())
                                .verifiedBy(employeeSkill.getVerifiedBy())
                                .verifiedDate(employeeSkill.getVerifiedDate())
                                .source(employeeSkill.getSource())
                                .notes(employeeSkill.getNotes())
                                .certificationUrl(employeeSkill.getCertificationUrl())
                                .certificationVerificationStatus(
                                        employeeSkill.getCertificationVerificationStatus() != null
                                                ? employeeSkill.getCertificationVerificationStatus().name()
                                                : null
                                )
                                .certificationVerifiedBy(
                                        employeeSkill.getCertificationVerifiedBy()
                                )
                                .certificationVerifiedDate(
                                        employeeSkill.getCertificationVerifiedDate()
                                )
                                .certificationNotes(
                                        employeeSkill.getCertificationNotes()
                                )
                                .createdDate(employeeSkill.getCreatedDate())
                                .createdBy(employeeSkill.getCreatedBy())
                                .updateDate(employeeSkill.getUpdatedDate())
                                .updatedBy(employeeSkill.getUpdatedBy())
                                .build())
                        .toList();

        return PageResponse.<EmployeeSkillResponse>builder()
                .content(responses)
                .page(employeeSkillPage.getNumber())
                .size(employeeSkillPage.getSize())
                .totalElements(employeeSkillPage.getTotalElements())
                .totalPages(employeeSkillPage.getTotalPages())
                .last(employeeSkillPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public EmployeeSkillResponse restoreEmployeeSkill(Long employeeId, Long skillId) {

        EmployeeSkill employeeSkill = employeeSkillRepository
                .findByEmployeeIdAndSkillIdIncludingDeleted(employeeId, skillId)
                .orElseThrow(() -> new RuntimeException("Employee skill not found"));

        if (!employeeSkill.getIsDeleted()) {
            throw new RuntimeException("Skill is already active");
        }

        employeeSkill.setIsDeleted(false);

        employeeSkill.setUpdatedBy("System");
        employeeSkill.setUpdatedDate(LocalDateTime.now());

        EmployeeSkill saved = employeeSkillRepository.save(employeeSkill);

        return EmployeeSkillResponse.builder()
                .employeeId(saved.getEmployee().getId())
                .skillId(saved.getSkill().getId())
                .skillName(saved.getSkill().getName())
                .isDeleted(saved.getIsDeleted())
                .createdDate(saved.getCreatedDate())
                .updateDate(saved.getUpdatedDate())
                .build();
    }

    @Override
    @Transactional
    public List<EmployeeSkillResponse> bulkRestoreEmployeeSkills(
            Long employeeId,
            List<Long> skillIds) {

        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository
                        .findAllByEmployeeIdAndSkillIdsIncludingDeleted(
                                employeeId,
                                skillIds
                        );

        if (employeeSkills.isEmpty()) {
            throw new RuntimeException(
                    "No employee skills found for restoration"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        employeeSkills.forEach(skill -> {
            if (Boolean.TRUE.equals(skill.getIsDeleted())) {
                skill.setIsDeleted(false);
                skill.setUpdatedDate(now);
                skill.setUpdatedBy("System");
            }
        });
        List<EmployeeSkill> savedSkills =
                employeeSkillRepository.saveAll(employeeSkills);

        return savedSkills.stream()
                .map(saved -> EmployeeSkillResponse.builder()
                        .employeeId(saved.getEmployee().getId())
                        .skillId(saved.getSkill().getId())
                        .skillName(saved.getSkill().getName())
                        .proficiencyLevel(saved.getProficiencyLevel())
                        .isVerified(saved.getIsVerified())
                        .isDeleted(saved.getIsDeleted())
                        .updateDate(saved.getUpdatedDate())
                        .build())
                .toList();
    }

    @Override
    public List<EmployeeSkillResponse> getDeletedEmployeeSkills(Long employeeId) {

        List<EmployeeSkill> deletedSkills =
                employeeSkillRepository.findByEmployeeIdAndIsDeletedTrue(employeeId);

        if (deletedSkills.isEmpty()) {
            throw new RuntimeException(
                    "No deleted skills found for employee ID: " + employeeId
            );
        }

        return deletedSkills.stream()
                .map(employeeSkill -> EmployeeSkillResponse.builder()
                        .employeeId(employeeSkill.getEmployee().getId())
                        .skillId(employeeSkill.getSkill().getId())
                        .skillName(employeeSkill.getSkill().getName())
                        .skillCategory(employeeSkill.getSkill().getCategory())
                        .experienceYears(employeeSkill.getExperienceYears())
                        .proficiencyLevel(employeeSkill.getProficiencyLevel())
                        .lastUsed(employeeSkill.getLastUsed())
                        .isVerified(employeeSkill.getIsVerified())
                        .source(employeeSkill.getSource())
                        .notes(employeeSkill.getNotes())
                        .verifiedBy(employeeSkill.getVerifiedBy())
                        .verifiedDate(employeeSkill.getVerifiedDate())
                        .certificationVerifiedBy(employeeSkill.getCertificationVerifiedBy())
                        .certificationVerifiedDate(employeeSkill.getCertificationVerifiedDate())
                        .certificationNotes(employeeSkill.getCertificationNotes())
                        .certificationUrl(employeeSkill.getCertificationUrl())
                        .certificationVerificationStatus(
                                employeeSkill.getCertificationVerificationStatus() != null
                                        ? employeeSkill.getCertificationVerificationStatus().name()
                                        : null
                        )
                        .isDeleted(employeeSkill.getIsDeleted())
                        .createdDate(employeeSkill.getCreatedDate())
                        .createdBy(employeeSkill.getCreatedBy())
                        .updateDate(employeeSkill.getUpdatedDate())
                        .updatedBy(employeeSkill.getUpdatedBy())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void permanentlyDeleteEmployeeSkill(Long employeeId, Long skillId) {

        int deletedCount = employeeSkillRepository
                .permanentlyDeleteByEmployeeIdAndSkillId(
                        employeeId,
                        skillId
                );

        if (deletedCount == 0) {
            throw new RuntimeException(
                    "Employee skill not found with employeeId: "
                            + employeeId
                            + " and skillId: "
                            + skillId
            );
        }
    }

    @Override
    @Transactional
    public BulkDeleteEmployeeSkillResponse bulkDeleteEmployeeSkills(
            Long employeeId,
            List<Long> skillIds) {

        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("Skill IDs cannot be empty");
        }

        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository.findByEmployeeIdAndSkillIds(employeeId, skillIds);

        if (employeeSkills.isEmpty()) {
            throw new RuntimeException("No employee skills found for employeeId: " + employeeId);
        }

        employeeSkills.forEach(skill -> skill.setIsDeleted(true));

        employeeSkillRepository.saveAll(employeeSkills);

        List<Long> deletedIds = employeeSkills.stream()
                .map(s -> s.getSkill().getId())
                .toList();

        return BulkDeleteEmployeeSkillResponse.builder()
                .employeeId(employeeId)
                .deletedSkillIds(deletedIds)
                .deletedCount(deletedIds.size())
                .build();
    }

    @Override
    @Transactional
    public BulkVerificationFinalResponse bulkVerifyEmployeeSkills(
            Long employeeId,
            List<BulkVerifyEmployeeSkillRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new RuntimeException("Request list cannot be empty");
        }

        List<Long> skillIds = requests.stream()
                .map(BulkVerifyEmployeeSkillRequest::getSkillId)
                .toList();

        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository.findByEmployee_IdAndSkill_IdInAndIsDeletedFalse(
                        employeeId,
                        skillIds
                );

        Map<Long, EmployeeSkill> skillMap = employeeSkills.stream()
                .collect(Collectors.toMap(
                        es -> es.getSkill().getId(),
                        es -> es
                ));

        List<Long> updatedSkills = new ArrayList<>();
        List<Long> failedSkills = new ArrayList<>();

        int successCount = 0;

        for (BulkVerifyEmployeeSkillRequest request : requests) {

            EmployeeSkill employeeSkill = skillMap.get(request.getSkillId());

            if (employeeSkill == null) {
                failedSkills.add(request.getSkillId());
                continue;
            }

            employeeSkill.setIsVerified(request.getVerified());
            employeeSkill.setVerifiedBy(request.getVerifiedBy());
            employeeSkill.setVerifiedDate(LocalDateTime.now());

            employeeSkill.setCertificationVerificationStatus(
                    request.getVerified()
                            ? CertificationStatus.VERIFIED
                            : CertificationStatus.REJECTED
            );

            employeeSkill.setCertificationVerifiedBy(request.getVerifiedBy());
            employeeSkill.setCertificationVerifiedDate(LocalDateTime.now());

            employeeSkillRepository.save(employeeSkill);

            updatedSkills.add(request.getSkillId());
            successCount++;
        }


        BulkVerificationFinalResponse.Summary summary =
                BulkVerificationFinalResponse.Summary.builder()
                        .employeeId(employeeId)
                        .totalRequested(requests.size())
                        .successCount(successCount)
                        .failedCount(failedSkills.size())
                        .failedSkillIds(failedSkills)
                        .build();

        BulkVerificationFinalResponse.Result result =
                BulkVerificationFinalResponse.Result.builder()
                        .employeeId(employeeId)
                        .updatedSkills(updatedSkills)
                        .status(successCount > 0 ? CertificationStatus.VERIFIED : CertificationStatus.REJECTED)
                        .updatedCount(successCount)
                        .build();

        return BulkVerificationFinalResponse.builder()
                .summary(summary)
                .result(result)
                .build();
    }

    @Override
    public SkillSummaryResponse getEmployeeSkillSummary(Long employeeId) {

        LocalDate today = LocalDate.now();

        // =========================
        // 1. SKILLS DATA
        // =========================
        List<EmployeeSkill> skills =
                employeeSkillRepository.findByEmployee_IdAndIsDeletedFalse(employeeId);

        int totalSkills = skills.size();
        int verifiedSkills = 0;
        int pendingSkills = 0;
        int expertSkills = 0;

        for (EmployeeSkill skill : skills) {

            if (Boolean.TRUE.equals(skill.getIsVerified())) {
                verifiedSkills++;
            } else {
                pendingSkills++;
            }

            if (skill.getProficiencyLevel() == ProficiencyLevel.EXPERT) {
                expertSkills++;
            }
        }

        // =========================
        // 2. CERTIFICATION DATA
        // =========================
        List<EmployeeCertification> certifications =
                employeeCertificationsRepository
                        .findByEmployee_IdAndIsDeletedFalse(employeeId);

        int expiredCertifications = 0;

        for (EmployeeCertification cert : certifications) {

            if (cert.getExpiryDate() != null &&
                    cert.getExpiryDate().isBefore(today)) {
                expiredCertifications++;
            }
        }

        // =========================
        // 3. RESPONSE
        // =========================
        return SkillSummaryResponse.builder()
                .employeeId(employeeId)
                .totalSkills(totalSkills)
                .verifiedSkills(verifiedSkills)
                .pendingSkills(pendingSkills)
                .expertSkills(expertSkills)
                .expiredCertifications(expiredCertifications)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillAnalyticsResponse getEmployeeSkillAnalytics(Long employeeId) {

        employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId
                        ));

        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository
                        .findByEmployee_IdAndIsDeletedFalse(employeeId);

        if (employeeSkills.isEmpty()) {
            return SkillAnalyticsResponse.builder()
                    .employeeId(employeeId)
                    .totalSkills(0)
                    .verifiedSkills(0)
                    .pendingSkills(0)
                    .expertSkills(0)
                    .intermediateSkills(0)
                    .beginnerSkills(0)
                    .averageExperience(0.0)
                    .build();
        }

        int verifiedSkills = 0;
        int pendingSkills = 0;
        int expertSkills = 0;
        int intermediateSkills = 0;
        int beginnerSkills = 0;

        double totalExperience = 0.0;

        for (EmployeeSkill employeeSkill : employeeSkills) {

            if (Boolean.TRUE.equals(employeeSkill.getIsVerified())) {
                verifiedSkills++;
            } else {
                pendingSkills++;
            }

            if (employeeSkill.getExperienceYears() != null) {
                totalExperience += employeeSkill.getExperienceYears();
            }

            ProficiencyLevel proficiency =
                    employeeSkill.getProficiencyLevel();

            if (proficiency != null) {
                switch (proficiency) {
                    case EXPERT -> expertSkills++;
                    case INTERMEDIATE -> intermediateSkills++;
                    case BEGINNER -> beginnerSkills++;
                }
            }
        }

        double averageExperience =
                totalExperience / employeeSkills.size();

        return SkillAnalyticsResponse.builder()
                .employeeId(employeeId)
                .totalSkills(employeeSkills.size())
                .verifiedSkills(verifiedSkills)
                .pendingSkills(pendingSkills)
                .expertSkills(expertSkills)
                .intermediateSkills(intermediateSkills)
                .beginnerSkills(beginnerSkills)
                .averageExperience(
                        Math.round(averageExperience * 100.0) / 100.0
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillGapAnalysisResponse getSkillGapAnalysis(Long employeeId) {

        employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId));

        // Employee Current Skills
        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository
                        .findByEmployee_IdAndIsDeletedFalse(employeeId);

        List<String> existingSkills = employeeSkills.stream()
                .map(employeeSkill -> employeeSkill.getSkill().getName())
                .distinct()
                .sorted()
                .toList();

        // Master Skill Catalog
        List<Skill> allSkills =
                skillRepository.findByIsDeletedFalse();

        List<String> allSkillNames = allSkills.stream()
                .map(Skill::getName)
                .distinct()
                .sorted()
                .toList();

        // Missing Skills
        List<String> missingSkills = allSkillNames.stream()
                .filter(skill -> !existingSkills.contains(skill))
                .toList();

        int totalAssignedSkills = existingSkills.size();
        int totalMissingSkills = missingSkills.size();

        double skillCoveragePercentage =
                allSkillNames.isEmpty()
                        ? 0.0
                        : ((double) totalAssignedSkills / allSkillNames.size()) * 100;

        return SkillGapAnalysisResponse.builder()
                .employeeId(employeeId)
                .totalAssignedSkills(totalAssignedSkills)
                .totalMissingSkills(totalMissingSkills)
                .skillCoveragePercentage(
                        Math.round(skillCoveragePercentage * 100.0) / 100.0
                )
                .existingSkills(existingSkills)
                .missingSkills(missingSkills)
                .recommendedSkills(missingSkills.stream()
                        .limit(5)
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillRecommendationResponse getSkillRecommendations(Long employeeId) {

        employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId));

        List<EmployeeSkill> employeeSkills =
                employeeSkillRepository
                        .findByEmployee_IdAndIsDeletedFalse(employeeId);

        List<Long> existingSkillIds = employeeSkills.stream()
                .map(es -> es.getSkill().getId())
                .toList();

        List<Skill> recommendedSkills =
                skillRepository.findByIsDeletedFalse()
                        .stream()
                        .filter(skill -> !existingSkillIds.contains(skill.getId()))
                        .limit(5)
                        .toList();

        List<SkillRecommendationResponse.RecommendedSkill> recommendations =
                recommendedSkills.stream()
                        .map(skill -> SkillRecommendationResponse.RecommendedSkill.builder()
                                .skillId(skill.getId())
                                .skillName(skill.getName())
                                .category(skill.getCategory())
                                .recommendationReason(
                                        "Recommended based on your current skill profile and career growth."
                                )
                                .recommendedCourses(List.of(
                                        skill.getName() + " Fundamentals",
                                        "Advanced " + skill.getName(),
                                        skill.getName() + " for Professionals"
                                ))
                                .recommendedCertifications(List.of(
                                        skill.getName() + " Associate Certification",
                                        skill.getName() + " Professional Certification"
                                ))
                                .priority(calculatePriority(skill))
                                .build())
                        .sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
                        .toList();

        return SkillRecommendationResponse.builder()
                .employeeId(employeeId)
                .totalRecommendations(recommendations.size())
                .recommendations(recommendations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeSkillResponse> searchEmployeesBySkills(
            SearchEmployeesBySkillsRequest request) {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "employee.firstName")
        );

        List<ProficiencyLevel> allowedLevels =
                getAllowedProficiencyLevels(request.getMinimumProficiency());

        Page<EmployeeSkill> employeeSkillPage;

        if (Boolean.TRUE.equals(request.getMatchAll())) {
            employeeSkillPage =
                    employeeSkillRepository.findEmployeesByAllSkills(
                            request.getSkillIds(),
                            (long) request.getSkillIds().size(),
                            allowedLevels,
                            pageable
                    );
        } else {
            employeeSkillPage =
                    employeeSkillRepository.findEmployeesByAnySkills(
                            request.getSkillIds(),
                            allowedLevels,
                            pageable
                    );
        }

        List<EmployeeSkillResponse> responses =
                employeeSkillPage.getContent()
                        .stream()
                        .map(this::mapToEmployeeSkillResponse)
                        .toList();

        return PageResponse.<EmployeeSkillResponse>builder()
                .content(responses)
                .page(employeeSkillPage.getNumber())
                .size(employeeSkillPage.getSize())
                .totalElements(employeeSkillPage.getTotalElements())
                .totalPages(employeeSkillPage.getTotalPages())
                .numberOfElements(employeeSkillPage.getNumberOfElements())
                .first(employeeSkillPage.isFirst())
                .last(employeeSkillPage.isLast())
                .hasNext(employeeSkillPage.hasNext())
                .hasPrevious(employeeSkillPage.hasPrevious())
                .sortBy("employee.firstName")
                .direction("ASC")
                .build();
    }

    private List<ProficiencyLevel> getAllowedProficiencyLevels(
            ProficiencyLevel minimumProficiency) {

        if (minimumProficiency == null) {
            return Arrays.asList(ProficiencyLevel.values());
        }

        return Arrays.stream(ProficiencyLevel.values())
                .filter(level ->
                        level.ordinal() >= minimumProficiency.ordinal())
                .toList();
    }

    private EmployeeSkillResponse mapToEmployeeSkillResponse(
            EmployeeSkill employeeSkill) {

        return EmployeeSkillResponse.builder()
                .employeeId(employeeSkill.getEmployee().getId())
                .employeeName(
                        employeeSkill.getEmployee().getFirstName()
                                + " "
                                + employeeSkill.getEmployee().getLastName()
                )
                .skillId(employeeSkill.getSkill().getId())
                .skillName(employeeSkill.getSkill().getName())
                .skillCategory(employeeSkill.getSkill().getCategory())
                .proficiencyLevel(employeeSkill.getProficiencyLevel())
                .experienceYears(employeeSkill.getExperienceYears())
                .isVerified(employeeSkill.getIsVerified())
                .lastUsed(employeeSkill.getLastUsed())
                .build();
    }

    private Integer calculatePriority(Skill skill) {

        String category = skill.getCategory() != null
                ? skill.getCategory().toLowerCase()
                : "";

        if (category.contains("cloud")) {
            return 1;
        }

        if (category.contains("devops")) {
            return 2;
        }

        if (category.contains("backend")) {
            return 3;
        }

        if (category.contains("frontend")) {
            return 4;
        }

        return 5;
    }
}

