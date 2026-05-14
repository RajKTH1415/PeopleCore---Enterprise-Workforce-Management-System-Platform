package com.peoplecore.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportHistoryResponse {

    private String fileName;

    private String format;

    private Long size;

    private String createdAt;

    private String downloadUrl;
}
