package com.peoplecore.service;

import com.peoplecore.dto.request.AddressRequest;
import com.peoplecore.dto.response.AddressResponse;

public interface AddressManagementService {


    AddressResponse addAddress(Long employeeId, AddressRequest request);
}