package com.peoplecore.service;


import com.peoplecore.dto.response.DocumentApprovalResponse;
import jakarta.servlet.http.HttpServletRequest;

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
}