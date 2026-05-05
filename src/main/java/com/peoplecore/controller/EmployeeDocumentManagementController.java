package com.peoplecore.controller;

import com.peoplecore.dto.response.DocumentDetailsResponse;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/documents")
public class EmployeeDocumentManagementController {


    private final EmployeesDocumentsService employeesDocumentsService;

    public EmployeeDocumentManagementController(EmployeesDocumentsService employeesDocumentsService) {
        this.employeesDocumentsService = employeesDocumentsService;
    }


    @PostMapping(value = "/{employeeId}/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String documentType,
            @RequestParam String category,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrimary,
            @RequestParam(required = false) List<String> tags,
            HttpServletRequest httpServletRequest) {

        DocumentResponse response = employeesDocumentsService.uploadDocument(
                employeeId, file, documentType, category, title,
                description, documentNumber, issueDate, expiryDate,
                isPrimary, tags, httpServletRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Document uploaded successfully", httpServletRequest.getRequestURI(), response));
    }

    /* its not use*/
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> deleteAllDocuments(
            HttpServletRequest request) {

        employeesDocumentsService.deleteAllDocumentsSystem();

        return ResponseEntity.ok(
                ApiResponse.success(200, "All documents deleted successfully",
                        request.getRequestURI(), null));
    }
    @GetMapping("/{employeeId}/documents/{documentId}")
    public ResponseEntity<ApiResponse<DocumentDetailsResponse>> getDocumentById(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            HttpServletRequest httpServletRequest
    ) {

        DocumentDetailsResponse response =
                employeesDocumentsService.getDocumentById(employeeId, documentId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document fetched successfully",httpServletRequest.getRequestURI(), response));
    }
}
