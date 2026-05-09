package com.peoplecore.controller;
import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.dto.response.PageResponse;
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
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> requestApproval(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.requestApproval(documentId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Approval request created successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/approve")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> approveDocument(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.approveDocument(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document approved successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/reject")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> rejectApproval(@PathVariable Long approvalId, @RequestParam String reason, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.rejectApproval(approvalId, reason, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval rejected successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getPendingApprovals(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size,
            @RequestParam(defaultValue = "requestedAt")
            String sortBy,
            @RequestParam(defaultValue = "DESC")
            String direction,
            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getPendingApprovals(
                        page,
                        size,
                        sortBy,
                        direction,
                httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Pending approvals fetched successfully", httpServletRequest.getRequestURI(), response));
    }


    @GetMapping("/{documentId}/approvals")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getApprovalHistory(
            @PathVariable String documentId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getApprovalHistory(
                        documentId,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval history fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approval/{approvalId}")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> getApprovalById(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.getApprovalById(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval details fetched successfully",httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/cancel")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> cancelApproval(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.cancelApproval(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Approval request cancelled successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approvals")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getApprovalsByStatus(

            @RequestParam String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response =
                documentApprovalService.getApprovalsByStatus(
                        status,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approvals fetched successfully", httpServletRequest.getRequestURI(), response));
    }
}