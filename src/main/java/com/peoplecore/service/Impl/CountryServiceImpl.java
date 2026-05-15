package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;
import com.peoplecore.module.CountryMaster;
import com.peoplecore.repository.CountryRepository;
import com.peoplecore.service.CountryService;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public CountryResponse createCountry(CountryRequest request) {

        if (countryRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Country code already exists");
        }

        CountryMaster country = CountryMaster.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .dialCode(request.getDialCode())
                .currencyCode(
                        request.getCurrencyCode() != null
                                ? request.getCurrencyCode().toUpperCase()
                                : null
                )
                .build();

        CountryMaster savedCountry = countryRepository.save(country);

        return mapToResponse(savedCountry);
    }

    private CountryResponse mapToResponse(
            CountryMaster country) {

        return CountryResponse.builder()
                .code(country.getCode())
                .name(country.getName())
                .dialCode(country.getDialCode())
                .currencyCode(country.getCurrencyCode())
                .isActive(country.getIsActive())
                .createdDate(country.getCreatedDate())
                .createdBy(country.getCreatedBy())
                .updatedDate(country.getUpdatedDate())
                .updatedBy(country.getUpdatedBy())
                .build();
    }
}