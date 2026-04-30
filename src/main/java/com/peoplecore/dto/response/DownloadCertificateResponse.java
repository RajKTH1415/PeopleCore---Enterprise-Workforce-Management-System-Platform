package com.peoplecore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadCertificateResponse {

    private String fileName;
    private String contentType;
    private long fileSize;
    private byte[] fileData;
}