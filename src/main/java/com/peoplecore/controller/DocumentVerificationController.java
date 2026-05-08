package com.peoplecore.controller;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.service.DocumentVerificationService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentVerificationController {

    private final DocumentVerificationService documentVerificationService;

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<DocumentResponse>> verifyDocument(
            @PathVariable String documentId,
            HttpServletRequest request
    ) {

        DocumentResponse response =
                documentVerificationService.verifyDocument(
                        documentId,
                        request
                );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document verified successfully",
                        request.getRequestURI(),
                        response
                ));
    }

    @PutMapping("/{documentId}/reject")
    public ResponseEntity<ApiResponse<DocumentResponse>> rejectDocument(
            @PathVariable String documentId,
            @RequestParam String reason,
            HttpServletRequest request
    ) {

        DocumentResponse response =
                documentVerificationService.rejectDocument(
                        documentId,
                        reason,
                        request
                );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document rejected successfully",
                        request.getRequestURI(),
                        response
                ));
    }
}