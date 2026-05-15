package com.peoplecore.service;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;

public interface CountryService {

    CountryResponse createCountry(CountryRequest request);



}

