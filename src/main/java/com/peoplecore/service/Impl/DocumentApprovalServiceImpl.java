package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.BulkApprovalRequest;
import com.peoplecore.dto.response.ApprovalDashboardResponse;
import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.module.DocumentApproval;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.repository.DocumentApprovalRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentApprovalServiceImpl implements DocumentApprovalService {

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
            HttpServletRequest request) {

        DocumentApproval approval = documentApprovalRepository.findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException("Approval not found"));

        approval.setApprovalStatus("REJECTED");
        approval.setApprovedBy(1L);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setRejectionReason(reason);

        DocumentApproval savedApproval = documentApprovalRepository.save(approval);

        EmployeeDocument document =
                employeeDocumentRepository
                        .findByDocumentId(approval.getDocumentId())
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        document.setStatus("REJECTED");

        employeeDocumentRepository.save(document);

        return mapToResponse(savedApproval);
    }

    @Override
    public PageResponse<DocumentApprovalResponse> getPendingApprovals(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentApproval> approvalPage =
                documentApprovalRepository.findByApprovalStatus(
                        "PENDING",
                        pageable
                );

        List<DocumentApprovalResponse> content =
                approvalPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentApprovalResponse>builder()
                .content(content)
                .page(approvalPage.getNumber())
                .size(approvalPage.getSize())
                .totalElements(approvalPage.getTotalElements())
                .totalPages(approvalPage.getTotalPages())
                .numberOfElements(approvalPage.getNumberOfElements())
                .first(approvalPage.isFirst())
                .last(approvalPage.isLast())
                .hasNext(approvalPage.hasNext())
                .hasPrevious(approvalPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public PageResponse<DocumentApprovalResponse> getApprovalHistory(
            String documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        EmployeeDocument document =
                employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentApproval> approvalPage =
                documentApprovalRepository.findByDocumentId(
                        document.getDocumentId(),
                        pageable
                );

        List<DocumentApprovalResponse> content =
                approvalPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentApprovalResponse>builder()
                .content(content)
                .page(approvalPage.getNumber())
                .size(approvalPage.getSize())
                .totalElements(approvalPage.getTotalElements())
                .totalPages(approvalPage.getTotalPages())
                .numberOfElements(approvalPage.getNumberOfElements())
                .first(approvalPage.isFirst())
                .last(approvalPage.isLast())
                .hasNext(approvalPage.hasNext())
                .hasPrevious(approvalPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public DocumentApprovalResponse getApprovalById(
            Long approvalId,
            HttpServletRequest request) {

        DocumentApproval approval = documentApprovalRepository.findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException("Approval not found"));
        return mapToResponse(approval);
    }

    @Override
    public DocumentApprovalResponse cancelApproval(
            Long approvalId,
            HttpServletRequest request
    ) {

        DocumentApproval approval =
                documentApprovalRepository.findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException("Approval not found"));

        if (!approval.getApprovalStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException(
                    "Only pending approvals can be cancelled"
            );
        }

        approval.setApprovalStatus("CANCELLED");

        DocumentApproval savedApproval =
                documentApprovalRepository.save(approval);

        EmployeeDocument document =
                employeeDocumentRepository
                        .findByDocumentId(approval.getDocumentId())
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        document.setStatus("UPLOADED");

        employeeDocumentRepository.save(document);

        return mapToResponse(savedApproval);
    }

    @Override
    public PageResponse<DocumentApprovalResponse> getApprovalsByStatus(
            String status,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentApproval> approvalPage =
                documentApprovalRepository.findByApprovalStatus(
                        status.toUpperCase(),
                        pageable
                );

        List<DocumentApprovalResponse> content =
                approvalPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentApprovalResponse>builder()
                .content(content)
                .page(approvalPage.getNumber())
                .size(approvalPage.getSize())
                .totalElements(approvalPage.getTotalElements())
                .totalPages(approvalPage.getTotalPages())
                .numberOfElements(approvalPage.getNumberOfElements())
                .first(approvalPage.isFirst())
                .last(approvalPage.isLast())
                .hasNext(approvalPage.hasNext())
                .hasPrevious(approvalPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public ApprovalDashboardResponse getApprovalDashboard(HttpServletRequest request) {

        long pending = documentApprovalRepository.countByApprovalStatus("PENDING");

        long approved = documentApprovalRepository.countByApprovalStatus("APPROVED");

        long rejected = documentApprovalRepository.countByApprovalStatus("REJECTED");

        long cancelled = documentApprovalRepository.countByApprovalStatus("CANCELLED");

        return ApprovalDashboardResponse.builder()
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .cancelled(cancelled)
                .build();
    }

    @Override
    public PageResponse<DocumentApprovalResponse> getMyApprovals(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        Long loggedInUserId = 1L;

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentApproval> approvalPage =
                documentApprovalRepository.findByRequestedBy(
                        loggedInUserId,
                        pageable
                );

        List<DocumentApprovalResponse> content =
                approvalPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentApprovalResponse>builder()
                .content(content)
                .page(approvalPage.getNumber())
                .size(approvalPage.getSize())
                .totalElements(approvalPage.getTotalElements())
                .totalPages(approvalPage.getTotalPages())
                .numberOfElements(approvalPage.getNumberOfElements())
                .first(approvalPage.isFirst())
                .last(approvalPage.isLast())
                .hasNext(approvalPage.hasNext())
                .hasPrevious(approvalPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public PageResponse<DocumentApprovalResponse> getMyPendingActions(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        Long loggedInApproverId = 1L;

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentApproval> approvalPage =
                documentApprovalRepository
                        .findByApproverIdAndApprovalStatus(
                                loggedInApproverId,
                                "PENDING",
                                pageable
                        );

        List<DocumentApprovalResponse> content =
                approvalPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentApprovalResponse>builder()
                .content(content)
                .page(approvalPage.getNumber())
                .size(approvalPage.getSize())
                .totalElements(approvalPage.getTotalElements())
                .totalPages(approvalPage.getTotalPages())
                .numberOfElements(approvalPage.getNumberOfElements())
                .first(approvalPage.isFirst())
                .last(approvalPage.isLast())
                .hasNext(approvalPage.hasNext())
                .hasPrevious(approvalPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    @Transactional
    public List<DocumentApprovalResponse> bulkApprove(
            BulkApprovalRequest request,
            HttpServletRequest httpServletRequest
    ) {

        List<DocumentApprovalResponse> responses = new ArrayList<>();

        for (Long approvalId : request.getApprovalIds()) {

            DocumentApproval approval =
                    documentApprovalRepository.findById(approvalId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Approval not found: " + approvalId
                                    ));

            if (!approval.getApprovalStatus()
                    .equalsIgnoreCase("PENDING")) {

                continue;
            }

            approval.setApprovalStatus("APPROVED");
            approval.setApprovedBy(1L);
            approval.setApprovedAt(LocalDateTime.now());

            DocumentApproval savedApproval =
                    documentApprovalRepository.save(approval);

            EmployeeDocument document =
                    employeeDocumentRepository
                            .findByDocumentId(
                                    approval.getDocumentId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException("Document not found"));

            document.setStatus("APPROVED");

            employeeDocumentRepository.save(document);

            responses.add(mapToResponse(savedApproval));
        }

        return responses;
    }

    private DocumentApprovalResponse mapToResponse(
            DocumentApproval approval) {

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