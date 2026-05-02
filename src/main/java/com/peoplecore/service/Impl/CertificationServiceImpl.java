package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.CertificationRequest;
import com.peoplecore.dto.response.CertificationResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.module.Certification;
import com.peoplecore.repository.CertificationRepository;
import com.peoplecore.service.CertificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }


    @Override
    public CertificationResponse createCertification(CertificationRequest request) {

        certificationRepository.findByNameAndIssuerAndIsDeletedFalse(request.getName(),request.getIssuer()).ifPresent(cert->{
            throw new RuntimeException("Certification already exists");
        });

        Certification certification = new Certification();
        certification.setName(request.getName());
        certification.setIssuer(request.getIssuer());

        Certification savedCertification = certificationRepository.save(certification);
        return CertificationResponse.builder()
                .id(savedCertification.getId())
                .name(savedCertification.getName())
                .issuer(savedCertification.getIssuer())
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Override
    public CertificationResponse getById(Long id) {
     Certification certification =   certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not found with ID :"+ id));

        if (Boolean.TRUE.equals(certification.getIsDeleted())) {
            throw new RuntimeException("Certificate not found with ID: " + id);
        }
        return CertificationResponse.builder()
                .name(certification.getName())
                .issuer(certification.getIssuer())
                .deleted(certification.getIsDeleted())
                .createdBy(certification.getCreatedBy())
                .createdDate(certification.getCreatedDate())
                .updatedDate(certification.getUpdatedDate())
                .updatedBy(certification.getUpdatedBy())
                .build();
    }

    @Override
    public CertificationResponse deleteCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not  found with ID :"+ id));

        if (Boolean.TRUE.equals(certification.getIsDeleted())){
            throw new RuntimeException("certificate already deleted with ID:"+ id);
        }
        certification.setIsDeleted(true);

        certification.setCreatedDate(LocalDateTime.now());
        certification.setCreatedBy("SYSTEM");
        certification.setUpdatedBy("SYSTEM");
        certification.setUpdatedDate(LocalDateTime.now());

        Certification savedCertificate = certificationRepository.save(certification);

        return CertificationResponse.builder()
                .name(savedCertificate.getName())
                .issuer(savedCertificate.getIssuer())
                .deleted(savedCertificate.getIsDeleted())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CertificationResponse> getAllCertifications(
            int page,
            int size,
            String sortBy,
            String direction,
            String name,
            String issuer,
            String search,
            Boolean includeDeleted //  NEW
    ) {

        // Handle null / blank inputs (VERY IMPORTANT)
        name = (name != null && !name.trim().isEmpty()) ? name : null;
        issuer = (issuer != null && !issuer.trim().isEmpty()) ? issuer : null;
        search = (search != null && !search.trim().isEmpty()) ? search : null;

        // Sorting
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // fallback if invalid field
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdDate";
        }

        Sort sort = Sort.by(sortDirection, sortBy)
                .and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Certification> certificationPage =
                certificationRepository.findCertificationsWithFilters(
                        includeDeleted,
                        name,
                        issuer,
                        search,
                        pageable
                );

        List<CertificationResponse> responses = certificationPage.getContent()
                .stream()
                .map(cert -> CertificationResponse.builder()
                        .id(cert.getId())
                        .name(cert.getName())
                        .issuer(cert.getIssuer())
                        .deleted(cert.getIsDeleted())
                        .createdDate(cert.getCreatedDate())
                        .createdBy(cert.getCreatedBy())
                        .updatedDate(cert.getUpdatedDate())
                        .updatedBy(cert.getUpdatedBy())
                        .build()
                )
                .toList();

        // Page Response
        return PageResponse.<CertificationResponse>builder()
                .content(responses)
                .page(certificationPage.getNumber())
                .size(certificationPage.getSize())
                .totalElements(certificationPage.getTotalElements())
                .totalPages(certificationPage.getTotalPages())
                .numberOfElements(certificationPage.getNumberOfElements())
                .first(certificationPage.isFirst())
                .last(certificationPage.isLast())
                .hasNext(certificationPage.hasNext())
                .hasPrevious(certificationPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public CertificationResponse updateCertification(Long id, CertificationRequest request) {
      Certification certification =   certificationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Certificate not found with ID :"+ id));

      if (Boolean.TRUE.equals(certification.getIsDeleted())){
          throw new RuntimeException("Cannot update a deleted certification");
      }

        String name = request.getName().trim();
        String issuer = request.getIssuer().trim();

       boolean exists =  certificationRepository.existsByNameIgnoreCaseAndIdNot(name,id);
       if (exists){
           throw new RuntimeException("Certificate already exists");
       }
       certification.setName(name);
       certification.setIssuer(issuer);

       certification.setUpdatedDate(LocalDateTime.now());
       certification.setUpdatedBy("SYSTEM");

       certification.setCreatedBy("SYSTEM");
       certification.setCreatedDate(LocalDateTime.now());

        Certification savedCertificate =  certificationRepository.save(certification);

        return CertificationResponse.builder()
                .name(savedCertificate.getName())
                .issuer(savedCertificate.getIssuer())
                .deleted(savedCertificate.getIsDeleted())
                .createdBy(savedCertificate.getCreatedBy())
                .createdDate(savedCertificate.getCreatedDate())
                .updatedBy(savedCertificate.getUpdatedBy())
                .updatedDate(savedCertificate.getUpdatedDate())
                .build();

    }
}
