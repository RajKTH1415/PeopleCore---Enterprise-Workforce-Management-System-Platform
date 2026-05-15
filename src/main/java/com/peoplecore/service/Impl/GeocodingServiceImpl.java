package com.peoplecore.service.Impl;


import com.peoplecore.dto.response.GeocodeResponse;
import com.peoplecore.service.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class GeocodingServiceImpl implements GeocodingService {

    @Override
    public GeocodeResponse geocodeAddress(String fullAddress) {

        log.info("Geocoding address: {}", fullAddress);

        // TODO:
        // Google Maps API Integration

        return GeocodeResponse.builder()
                .latitude(BigDecimal.valueOf(12.9716))
                .longitude(BigDecimal.valueOf(77.5946))
                .placeId("DUMMY_PLACE_ID")
                .formattedAddress(fullAddress)
                .build();
    }
}
