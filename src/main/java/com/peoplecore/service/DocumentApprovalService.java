package com.peoplecore.service;


import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DocumentApprovalService {

    DocumentApprovalResponse requestApproval(
            String documentId,
            HttpServletRequest request
    );

    DocumentApprovalResponse approveDocument(
            Long approvalId,
            HttpServletRequest request
    );

    DocumentApprovalResponse rejectApproval(
            Long approvalId,
            String reason,
            HttpServletRequest request
    );
    PageResponse<DocumentApprovalResponse> getPendingApprovals(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    PageResponse<DocumentApprovalResponse> getApprovalHistory(
            String documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );
}