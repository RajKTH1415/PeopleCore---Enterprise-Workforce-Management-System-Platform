package com.peoplecore.service.Impl;
import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.module.DocumentApproval;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.repository.DocumentApprovalRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentApprovalServiceImpl
        implements DocumentApprovalService {

    private final DocumentApprovalRepository documentApprovalRepository;

    private final EmployeeDocumentRepository employeeDocumentRepository;

    @Override
    public DocumentApprovalResponse requestApproval(
            String documentId,
            HttpServletRequest request
    ) {

        EmployeeDocument document =
                employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        document.setStatus("PENDING_APPROVAL");

        employeeDocumentRepository.save(document);

        DocumentApproval approval =
                DocumentApproval.builder()
                        .documentId(documentId)
                        .approvalStatus("PENDING")
                        .requestedBy(1L)
                        .requestedAt(LocalDateTime.now())
                        .remarks("Approval requested")
                        .build();

        DocumentApproval savedApproval =
                documentApprovalRepository.save(approval);

        return mapToResponse(savedApproval);
    }

    @Override
    public DocumentApprovalResponse approveDocument(
            Long approvalId,
            HttpServletRequest request
    ) {

        DocumentApproval approval =
                documentApprovalRepository.findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException("Approval not found"));

        approval.setApprovalStatus("APPROVED");

        approval.setApprovedBy(1L);

        approval.setApprovedAt(LocalDateTime.now());

        DocumentApproval savedApproval =
                documentApprovalRepository.save(approval);

        EmployeeDocument document =
                employeeDocumentRepository
                        .findByDocumentId(approval.getDocumentId())
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        document.setStatus("APPROVED");

        employeeDocumentRepository.save(document);

        return mapToResponse(savedApproval);
    }

    @Override
    public DocumentApprovalResponse rejectApproval(
            Long approvalId,
            String reason,
            HttpServletRequest request
    ) {

        DocumentApproval approval =
                documentApprovalRepository.findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException("Approval not found"));

        approval.setApprovalStatus("REJECTED");

        approval.setApprovedBy(1L);

        approval.setApprovedAt(LocalDateTime.now());

        approval.setRejectionReason(reason);

        DocumentApproval savedApproval =
                documentApprovalRepository.save(approval);

        EmployeeDocument document =
                employeeDocumentRepository
                        .findByDocumentId(approval.getDocumentId())
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        document.setStatus("REJECTED");

        employeeDocumentRepository.save(document);

        return mapToResponse(savedApproval);
    }

    private DocumentApprovalResponse mapToResponse(
            DocumentApproval approval
    ) {

        return DocumentApprovalResponse.builder()
                .approvalId(approval.getId())
                .documentId(approval.getDocumentId())
                .approvalStatus(approval.getApprovalStatus())
                .approvedBy(
                        approval.getApprovedBy() != null
                                ? approval.getApprovedBy().toString()
                                : null
                )
                .rejectionReason(approval.getRejectionReason())
                .approvedAt(approval.getApprovedAt())
                .build();
    }
}