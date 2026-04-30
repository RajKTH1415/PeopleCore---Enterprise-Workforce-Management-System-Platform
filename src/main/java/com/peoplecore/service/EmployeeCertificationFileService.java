package com.peoplecore.service;

import com.peoplecore.dto.response.DownloadCertificateResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
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
}
