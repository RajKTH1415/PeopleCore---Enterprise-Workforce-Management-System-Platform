package com.peoplecore.service.Impl;

import com.peoplecore.service.SkillParserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillParserServiceImpl implements SkillParserService {

    @Override
    public List<String> extractSkills(String text) {

        // Basic keyword extraction (replace with NLP later)
        List<String> knownSkills = List.of("Java", "Spring Boot", "SQL", "Microservices");

        return knownSkills.stream()
                .filter(text.toLowerCase()::contains)
                .toList();
    }
}
