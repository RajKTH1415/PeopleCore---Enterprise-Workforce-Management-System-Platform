package com.peoplecore.service;

import com.peoplecore.dto.response.GeocodeResponse;

public interface GeocodingService {

    GeocodeResponse geocodeAddress(String fullAddress);

}