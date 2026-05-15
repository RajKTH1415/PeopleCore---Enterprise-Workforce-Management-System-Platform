package com.peoplecore.service;

import com.peoplecore.dto.request.CityRequest;
import com.peoplecore.dto.response.CityResponse;

import java.util.List;

public interface CityService {


    List<CityResponse> getAllCities();

    List<CityResponse> getCitiesByState(Long stateId);

    CityResponse createCity(CityRequest request);

    CityResponse updateCity(Long id, CityRequest request);

    void deleteCity(Long id);

    void deleteAllCities();
}
