package com.peoplecore.controller;

import com.peoplecore.dto.request.DocumentSearchRequest;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.enums.DocumentStatus;
import com.peoplecore.enums.DocumentType;
import com.peoplecore.enums.ExpiryStatus;
import com.peoplecore.service.DocumentSearchService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> searchDocuments(

            @RequestParam(required = false) String q,

            @RequestParam(required = false) String employeeId,

            @RequestParam(required = false) DocumentType documentType,

            @RequestParam(required = false) Boolean verified,

            @RequestParam(required = false) DocumentStatus status,

            @RequestParam(required = false) ExpiryStatus expiryStatus,

            @RequestParam(required = false) LocalDate uploadedFrom,

            @RequestParam(required = false) LocalDate uploadedTo,

            @RequestParam(required = false) LocalDate expiryFrom,

            @RequestParam(required = false) LocalDate expiryTo,

            @RequestParam(defaultValue = "0") Integer page,

            @RequestParam(defaultValue = "10") Integer size,

            @RequestParam(defaultValue = "uploadedAt") String sortBy,

            @RequestParam(defaultValue = "DESC") String direction, HttpServletRequest httpServletRequest) {

        DocumentSearchRequest request = DocumentSearchRequest.builder()
                .q(q)
                .employeeId(employeeId)
                .documentType(documentType)
                .verified(verified)
                .status(status)
                .expiryStatus(expiryStatus)
                .uploadedFrom(uploadedFrom)
                .uploadedTo(uploadedTo)
                .expiryFrom(expiryFrom)
                .expiryTo(expiryTo)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        PageResponse<DocumentResponse> response =
                documentSearchService.searchDocuments(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Documents fetched successfully",httpServletRequest.getRequestURI(), response));
    }


    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getExpiringDocuments(

            @RequestParam(defaultValue = "30") Integer days,

            @RequestParam(defaultValue = "0") Integer page,

            @RequestParam(defaultValue = "10") Integer size,

            @RequestParam(defaultValue = "expiryDate") String sortBy,

            @RequestParam(defaultValue = "ASC") String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentResponse> response =
                documentSearchService.getExpiringDocuments(
                        days,
                        page,
                        size,
                        sortBy,
                        direction);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Expiring documents fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getExpiredDocuments(

            @RequestParam(defaultValue = "0") Integer page,

            @RequestParam(defaultValue = "10") Integer size,

            @RequestParam(defaultValue = "expiryDate") String sortBy,

            @RequestParam(defaultValue = "DESC") String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentResponse> response =
                documentSearchService.getExpiredDocuments(
                        page,
                        size,
                        sortBy,
                        direction);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Expired documents fetched successfully",  httpServletRequest.getRequestURI(), response));
    }
}