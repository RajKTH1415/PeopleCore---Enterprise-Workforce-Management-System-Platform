package com.peoplecore.service;

import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.response.CertificationResponse;
import com.peoplecore.dto.response.PageResponse;

public interface CertificationService {

    CertificationResponse createCertification(CertificationRequest request);

    CertificationResponse getById(Long id);

    CertificationResponse deleteCertification(Long id);

    PageResponse<CertificationResponse> getAllCertifications(
            int page,
            int size,
            String sortBy,
            String direction,
            String name,
            String issuer,
            String search,
            Boolean includeDeleted);


    CertificationResponse updateCertification(Long id, CertificationRequest request);

    CertificationResponse restoreCertification(Long id);
}

