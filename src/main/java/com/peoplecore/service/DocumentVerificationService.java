package com.peoplecore.service;


import com.peoplecore.dto.response.DocumentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface DocumentVerificationService {

    DocumentResponse verifyDocument(
            String documentId,
            HttpServletRequest request
    );

    DocumentResponse rejectDocument(
            String documentId,
            String reason,
            HttpServletRequest request
    );
}
