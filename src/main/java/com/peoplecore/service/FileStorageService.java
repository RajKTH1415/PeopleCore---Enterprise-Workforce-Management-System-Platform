package com.peoplecore.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {


    String uploadFile(MultipartFile file, String folder);

    byte[] downloadFile(String fileUrl);

    void deleteFile(String fileUrl);

    String generatePresignedUrl(String fileUrl);
}

