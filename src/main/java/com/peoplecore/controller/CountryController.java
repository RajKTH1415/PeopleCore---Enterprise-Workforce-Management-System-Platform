package com.peoplecore.controller;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;
import com.peoplecore.service.CountryService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CountryResponse>> createCountry(@RequestBody CountryRequest countryRequest, HttpServletRequest httpServletRequest){
        CountryResponse countryResponse = countryService.createCountry(countryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"Country created successfully", httpServletRequest.getRequestURI(), countryResponse));

    }
}
