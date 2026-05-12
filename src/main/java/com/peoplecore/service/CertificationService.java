package com.peoplecore.service;

import com.peoplecore.dto.request.BulkUpdateCertificationRequest;
import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.request.CertificationSkillRequest;
import com.peoplecore.dto.request.UpdateCertificationStatusRequest;
import com.peoplecore.dto.response.CertificationResponse;
import com.peoplecore.dto.response.CertificationSkillResponse;
import com.peoplecore.dto.response.CertificationUsageAnalyticsResponse;
import com.peoplecore.dto.response.PageResponse;

import java.util.List;

public interface CertificationService {
    List<CertificationResponse> bulkCreateCertifications(
            List<CertificationRequest> requests
    );

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

    List<CertificationResponse> bulkUpdateCertifications(
            List<BulkUpdateCertificationRequest> requests
    );

    CertificationResponse restoreCertification(Long id);

    void permanentlyDeleteCertification(Long id);

    CertificationResponse updateCertificationStatus(
            Long id,
            UpdateCertificationStatusRequest request
    );

    CertificationUsageAnalyticsResponse getCertificationUsageAnalytics();

    CertificationSkillResponse addSkills(Long id, CertificationSkillRequest request);
}

