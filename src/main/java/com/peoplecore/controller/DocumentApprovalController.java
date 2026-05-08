package com.peoplecore.controller;
import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentApprovalController {

    private final DocumentApprovalService documentApprovalService;

    @PostMapping("/{documentId}/request-approval")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>>
    requestApproval(@PathVariable String documentId,
            HttpServletRequest httpServletRequest) {

        DocumentApprovalResponse response =
                documentApprovalService.requestApproval(
                        documentId,
                        httpServletRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(),
                        "Approval request created successfully",
                        httpServletRequest.getRequestURI(),
                        response));
    }

    @PostMapping("/approval/{approvalId}/approve")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>>
    approveDocument(@PathVariable Long approvalId,
            HttpServletRequest httpServletRequest) {

        DocumentApprovalResponse response =
                documentApprovalService.approveDocument(
                        approvalId,
                        httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document approved successfully",
                        httpServletRequest.getRequestURI(),
                        response
                ));
    }

    @PostMapping("/approval/{approvalId}/reject")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>>
    rejectApproval(@PathVariable Long approvalId, @RequestParam String reason, HttpServletRequest httpServletRequest) {

        DocumentApprovalResponse response =
                documentApprovalService.rejectApproval(
                        approvalId,
                        reason,
                        httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Approval rejected successfully",
                        httpServletRequest.getRequestURI(),
                        response));
    }
}