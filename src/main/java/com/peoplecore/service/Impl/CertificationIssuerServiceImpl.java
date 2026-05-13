package com.peoplecore.service.Impl;


import com.peoplecore.dto.request.CertificationIssuerRequest;
import com.peoplecore.dto.response.CertificationIssuerResponse;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.module.CertificationIssuer;
import com.peoplecore.repository.CertificationIssuerRepository;
import com.peoplecore.service.CertificationIssuerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationIssuerServiceImpl
        implements CertificationIssuerService {

    private final CertificationIssuerRepository issuerRepository;

    @Override
    public CertificationIssuerResponse createIssuer(
            CertificationIssuerRequest request
    ) {

        if (issuerRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException(
                    "Issuer already exists"
            );
        }

        CertificationIssuer issuer =
                CertificationIssuer.builder()
                        .name(request.getName().trim())
                        .description(request.getDescription())
                        .website(request.getWebsite())
                        .active(true)
                        .build();

        issuerRepository.save(issuer);

        return mapToResponse(issuer);
    }

    @Override
    public List<CertificationIssuerResponse> getAllIssuers() {

        return issuerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CertificationIssuerResponse mapToResponse(
            CertificationIssuer issuer
    ) {

        return CertificationIssuerResponse.builder()
                .id(issuer.getId())
                .name(issuer.getName())
                .description(issuer.getDescription())
                .website(issuer.getWebsite())
                .active(issuer.getActive())
                .build();
    }
}
