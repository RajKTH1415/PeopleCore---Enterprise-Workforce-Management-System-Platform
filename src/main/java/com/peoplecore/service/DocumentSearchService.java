package com.peoplecore.service;

import com.peoplecore.dto.request.DocumentSearchRequest;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.PageResponse;

public interface DocumentSearchService {

    PageResponse<DocumentResponse> searchDocuments(
            DocumentSearchRequest request
    );
}
