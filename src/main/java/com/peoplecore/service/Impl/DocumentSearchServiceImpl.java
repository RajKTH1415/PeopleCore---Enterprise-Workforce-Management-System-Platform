package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.DocumentSearchRequest;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Service
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final EmployeeDocumentRepository employeeDocumentRepository;

    public DocumentSearchServiceImpl(EmployeeDocumentRepository employeeDocumentRepository) {
        this.employeeDocumentRepository = employeeDocumentRepository;
    }


    @Override
    public PageResponse<DocumentResponse> searchDocuments(
            DocumentSearchRequest request
    ) {

        Sort sort = request.getDirection().equalsIgnoreCase("ASC")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        Page<EmployeeDocument> pageResult =
                employeeDocumentRepository.findAll(pageable);

        List<DocumentResponse> content =
                pageResult.getContent()
                        .stream()
                        .map(document -> DocumentResponse.builder()

                                .documentId(document.getDocumentId())

                                .employeeId(
                                        document.getEmployeeId()
                                )

                                .documentType(
                                        document.getDocumentType()
                                )

                                .category(document.getDocumentCategory())

                                .title(document.getTitle())

                                .fileUrl(document.getFileUrl())

                                .fileName(document.getFileName())

                                .fileSize(document.getFileSize())

                                .version(document.getVersion())

                                .issueDate(document.getIssueDate())

                                .isPrimary(document.getIsPrimary())

                                .expiryDate(document.getExpiryDate())

                                .status(
                                        document.getStatus()
                                )

                                .verificationStatus(
                                        document.getVerificationStatus()
                                )

                                .tags(Arrays.asList(document.getTags()))

                                .uploadedAt(document.getUploadedAt())

                                .updatedAt(document.getUpdatedAt())

                                .build())
                        .toList();

        return PageResponse.<DocumentResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .numberOfElements(pageResult.getNumberOfElements())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .hasNext(pageResult.hasNext())
                .hasPrevious(pageResult.hasPrevious())
                .sortBy(request.getSortBy())
                .direction(request.getDirection())
                .build();
    }

    @Override
    public PageResponse<DocumentResponse> getExpiringDocuments(
            Integer days,
            Integer page,
            Integer size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDate today = LocalDate.now();
        LocalDate expiryLimit = today.plusDays(days);

        Page<EmployeeDocument> documents =
                employeeDocumentRepository.findByExpiryDateBetweenAndIsDeletedFalse(
                        today,
                        expiryLimit,
                        pageable
                );

        List<DocumentResponse> responseList =
                documents.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentResponse>builder()
                .content(responseList)
                .page(page)
                .size(size)
                .totalElements(documents.getTotalElements())
                .totalPages(documents.getTotalPages())
                .last(documents.isLast())
                .build();
    }

    @Override
    public PageResponse<DocumentResponse> getExpiredDocuments(
            Integer page,
            Integer size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDate today = LocalDate.now();

        Page<EmployeeDocument> documents =
                employeeDocumentRepository.findByExpiryDateBeforeAndIsDeletedFalse(
                        today,
                        pageable
                );

        List<DocumentResponse> responseList =
                documents.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentResponse>builder()
                .content(responseList)
                .page(page)
                .size(size)
                .totalElements(documents.getTotalElements())
                .totalPages(documents.getTotalPages())
                .last(documents.isLast())
                .build();
    }

    private DocumentResponse mapToResponse(EmployeeDocument document) {

        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .title(document.getTitle())
                .documentType(document.getDocumentType())
                .category(document.getDocumentCategory())
                .documentNumber(document.getDocumentNumber())
                .issueDate(document.getIssueDate())
                .expiryDate(document.getExpiryDate())
                .verifiedBy(document.getVerifiedBy())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
