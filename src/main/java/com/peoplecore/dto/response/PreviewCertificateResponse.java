package com.peoplecore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewCertificateResponse {

    private String message;
    private String fileName;
    private String contentType;
    private byte[] fileData;
    private long fileSize;
}
