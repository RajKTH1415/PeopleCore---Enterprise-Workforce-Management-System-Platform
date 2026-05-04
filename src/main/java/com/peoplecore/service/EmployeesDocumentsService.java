package com.peoplecore.service;

import com.peoplecore.dto.response.DocumentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface EmployeesDocumentsService {


    DocumentResponse uploadDocument(
            Long employeeId,
            MultipartFile file,
            String documentType,
            String category,
            String title,
            String description,
            String documentNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            Boolean isPrimary,
            List<String> tags,
            HttpServletRequest request
    );
}
