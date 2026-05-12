package com.peoplecore.controller;
import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.response.CertificationResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.service.CertificationService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationsController {

    private final CertificationService certificationService;

    public CertificationsController(CertificationService certificationService){
        this.certificationService = certificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificationResponse>> createCertification(@RequestBody CertificationRequest certificationRequest , HttpServletRequest httpServletRequest){
         CertificationResponse certificationResponse = certificationService.createCertification(certificationRequest);
         return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Certification created successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> getCertificationById(@PathVariable("id") Long Id , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.getById(Id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification fetched successfully",httpServletRequest.getRequestURI(), certificationResponse));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> deleteCertificateById(@PathVariable("id") Long id , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.deleteCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification deleted successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CertificationResponse>>> getAllCertifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String issuer,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") Boolean includeDeleted,
            HttpServletRequest httpServletRequest) {

        PageResponse<CertificationResponse> response =
                certificationService.getAllCertifications(
                        page, size, sortBy, direction, name, issuer, search, includeDeleted);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"All certifications fetched successfully",httpServletRequest.getRequestURI(),response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> updateCertificate(@PathVariable("id") Long id , @RequestBody CertificationRequest certificationRequest , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.updateCertification(id, certificationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification updated successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }
    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<CertificationResponse>> restoreCertification(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        CertificationResponse response = certificationService.restoreCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification restored successfully", httpServletRequest.getRequestURI(), response));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteCertification(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        certificationService.permanentlyDeleteCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification permanently deleted successfully", httpServletRequest.getRequestURI(), null ));
    }
}
