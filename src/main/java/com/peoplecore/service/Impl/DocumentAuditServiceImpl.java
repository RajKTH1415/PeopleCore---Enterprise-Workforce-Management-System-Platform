package com.peoplecore.service.Impl;

import com.peoplecore.dto.response.EmployeeDocumentAuditResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.module.EmployeeDocumentAudit;
import com.peoplecore.repository.DocumentAuditRepository;
import com.peoplecore.service.DocumentAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DocumentAuditServiceImpl implements DocumentAuditService {

    private final DocumentAuditRepository documentAuditRepository;

    public DocumentAuditServiceImpl(DocumentAuditRepository documentAuditRepository) {
        this.documentAuditRepository = documentAuditRepository;
    }

//    private final DocumentAccessLogRepository documentAccessLogRepository;

    @Override
    public PageResponse<EmployeeDocumentAuditResponse> getAuditLogs(
            Long documentId,
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

        Page<EmployeeDocumentAudit> auditLogs =
                documentAuditRepository.findByDocumentId(
                        documentId,
                        pageable
                );

        Page<EmployeeDocumentAuditResponse> response =
                auditLogs.map(log ->
                        EmployeeDocumentAuditResponse.builder()
                                .id(log.getId())
                                .documentId(log.getDocumentId())
                                .employeeId(log.getEmployeeId())
                                .action(log.getAction())
                                .fileName(log.getFileName())
                                .fileUrl(log.getFileUrl())
                                .remarks(log.getRemarks())
                                .performedBy(log.getPerformedBy())
                                .performedAt(log.getPerformedAt())
                                .actionType(log.getActionType())
                                .status(log.getStatus())
                                .build());

        return PageResponse.<EmployeeDocumentAuditResponse>builder()
                .content(response.getContent())
                .page(response.getNumber())
                .size(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .last(response.isLast())
                .build();
    }
}
