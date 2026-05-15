package com.peoplecore.controller;


import com.peoplecore.dto.request.AddressRequest;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address")
public class AddressManagementController {


    private final AddressManagementService addressManagementService;

    public AddressManagementController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@PathVariable Long employeeId, @RequestBody AddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.addAddress(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Address added successfully",httpServletRequest.getRequestURI(), response));
    }
}
