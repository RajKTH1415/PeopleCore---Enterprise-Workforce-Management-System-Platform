package com.peoplecore.service.Impl;

import com.peoplecore.service.OcrService;
import org.springframework.stereotype.Service;

@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public String extractText(byte[] fileBytes) {
        // Dummy (later use Tesseract / AWS Textract)
        return "Java Spring Boot SQL Resume Content";
    }
}
