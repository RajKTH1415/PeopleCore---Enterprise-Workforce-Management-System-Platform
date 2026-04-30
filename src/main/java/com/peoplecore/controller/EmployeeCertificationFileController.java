package com.peoplecore.controller;
import com.peoplecore.dto.response.DownloadCertificateResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PreviewCertificateResponse;
import com.peoplecore.service.EmployeeCertificationFileService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employee/file")
public class EmployeeCertificationFileController {

    private final EmployeeCertificationFileService employeeCertificationFileService;

    public EmployeeCertificationFileController(EmployeeCertificationFileService employeeCertificationFileService) {
        this.employeeCertificationFileService = employeeCertificationFileService;
    }

    @PostMapping("/{employeeId}/certifications/{certificationId}/upload")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> uploadCertificate(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpServletRequest) {

        EmployeeCertificationResponse response =
                employeeCertificationFileService.uploadCertificate(
                        employeeId,
                        certificationId,
                        file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Certificate uploaded successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/certifications/{certificationId}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId) {

        DownloadCertificateResponse response =
                employeeCertificationFileService.downloadCertificate(
                        employeeId,
                        certificationId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\""
                )
                .contentLength(response.getFileSize())
                .body(response.getFileData());
    }

    @GetMapping("/{employeeId}/certifications/{certificationId}/preview")
    public ResponseEntity<ByteArrayResource> previewCertificate(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId) {

        PreviewCertificateResponse response =
                employeeCertificationFileService.previewCertificate(
                        employeeId,
                        certificationId);

        ByteArrayResource resource =
                new ByteArrayResource(response.getFileData());

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(response.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + response.getFileName() + "\"")
                .header("X-Message", response.getMessage())
                .contentLength(response.getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{employeeId}/certifications/{certificationId}/file")
    public ResponseEntity<ApiResponse<Void>> deleteCertificateFile(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId,
            HttpServletRequest httpServletRequest) {

        employeeCertificationFileService.deleteCertificateFile(
                employeeId,
                certificationId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Certificate file deleted successfully",
                        httpServletRequest.getRequestURI(),
                        null
                )
        );
    }
}
