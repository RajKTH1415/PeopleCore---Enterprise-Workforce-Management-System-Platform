package com.peoplecore.dto.request;

import lombok.Data;

@Data
public class UpdateSkillRequest {
    private String name;
    private String category;
    private String description;
}
