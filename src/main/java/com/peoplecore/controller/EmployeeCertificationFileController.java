package com.peoplecore.controller;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.service.EmployeeCertificationFileService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"Certificate uploaded successfully", httpServletRequest.getRequestURI(), response));
    }
}
