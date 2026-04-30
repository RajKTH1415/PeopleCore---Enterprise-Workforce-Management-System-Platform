package com.peoplecore.service;

import com.peoplecore.dto.response.DownloadCertificateResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PreviewCertificateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeCertificationFileService {


    EmployeeCertificationResponse uploadCertificate(
            Long employeeId,
            Long certificationId,
            MultipartFile file
    );

    DownloadCertificateResponse downloadCertificate(
            Long employeeId,
            Long certificationId
    );

    PreviewCertificateResponse previewCertificate(
            Long employeeId,
            Long certificationId);

    void deleteCertificateFile(
            Long employeeId,
            Long certificationId);

    EmployeeCertificationResponse replaceCertificateFile(
            Long employeeId,
            Long certificationId,
            MultipartFile file);
}
