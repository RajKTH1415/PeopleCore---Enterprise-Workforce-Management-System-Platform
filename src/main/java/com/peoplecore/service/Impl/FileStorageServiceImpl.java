package com.peoplecore.service.Impl;

import com.peoplecore.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;


    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try{

            String fileName = UUID.randomUUID() +"_"+ file.getOriginalFilename();
            Path directory = Paths.get(uploadDir,folder);
            Files.createDirectories(directory);

            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }

    }

    @Override
    public byte[] downloadFile(String fileUrl) {
      try{

          return Files.readAllBytes(Paths.get(fileUrl));

      } catch (Exception e) {
          throw new RuntimeException("Failed to download file", e);
      }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {

            Files.delete(Paths.get(fileUrl));

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }

    }

    @Override
    public String generatePresignedUrl(String fileUrl) {
        return fileUrl;
    }
}
