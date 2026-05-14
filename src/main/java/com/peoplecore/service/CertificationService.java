package com.peoplecore.service;

import com.peoplecore.dto.request.BulkUpdateCertificationRequest;
import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.request.CertificationSkillRequest;
import com.peoplecore.dto.request.UpdateCertificationStatusRequest;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.CertificationStatus;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CertificationService {
    List<CertificationResponse> bulkCreateCertifications(
            List<CertificationRequest> requests
    );

    ResponseEntity<Resource> downloadCertificationExport(
            String fileName
    );

    List<CertificationAuditResponse> getCertificationAudit(Long certificationId);

    List<String> getSuggestions(String query);

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

    CertificationSkillResponse getSkills(Long id);

    String exportCertifications(
            String format,
            CertificationStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
    List<ExportHistoryResponse> getExportHistory();

    List<ExportHistoryResponse> getDownloadHistory();

    void deleteExportFile(String fileName);
}

