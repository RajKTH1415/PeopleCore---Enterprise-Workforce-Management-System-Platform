package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Builder
public class DownloadDocumentResponse {

    private Resource resource;

    private String fileName;

    private String contentType;
}
