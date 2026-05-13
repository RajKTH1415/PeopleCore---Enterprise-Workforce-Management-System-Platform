package com.peoplecore.service;

import com.peoplecore.dto.request.CertificationIssuerRequest;
import com.peoplecore.dto.response.CertificationIssuerResponse;

import java.util.List;

public interface CertificationIssuerService {

    CertificationIssuerResponse createIssuer(
            CertificationIssuerRequest request
    );

    List<CertificationIssuerResponse> getAllIssuers();
}
