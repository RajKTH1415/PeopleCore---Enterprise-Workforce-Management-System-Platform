package com.peoplecore.service;

public interface OcrService {
    String extractText(byte[] fileBytes);
}