package com.peoplecore.service;

import com.peoplecore.dto.request.DocumentSearchRequest;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.PageResponse;

public interface DocumentSearchService {

    PageResponse<DocumentResponse> searchDocuments(
            DocumentSearchRequest request
    );

    PageResponse<DocumentResponse> getExpiringDocuments(
            Integer days,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    );

    PageResponse<DocumentResponse> getExpiredDocuments(
            Integer page,
            Integer size,
            String sortBy,
            String direction
    );
}
