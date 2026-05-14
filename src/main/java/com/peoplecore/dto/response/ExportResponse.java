package com.peoplecore.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportResponse {

    private boolean success;

    private String message;

    private String downloadUrl;

    private String fileName;

    private String format;

    private String generatedAt;

    private String expiresIn;
}