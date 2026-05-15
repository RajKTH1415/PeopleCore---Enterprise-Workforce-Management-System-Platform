package com.peoplecore.service;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;

import java.util.List;

public interface CountryService {

    CountryResponse createCountry(CountryRequest request);

    List<CountryResponse> getAllCountries();

    CountryResponse getCountryById(Long id);

    CountryResponse updateCountry(Long id, CountryRequest request);



}

