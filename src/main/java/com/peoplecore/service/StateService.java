package com.peoplecore.service;

import com.peoplecore.dto.request.StateRequest;
import com.peoplecore.dto.response.StateResponse;

import java.util.List;

public interface StateService {


    List<StateResponse> getAllStates();

    List<StateResponse> getStatesByCountry(Long countryId);

    StateResponse createState(StateRequest request);

    StateResponse updateState(Long id, StateRequest request);

    void deleteState(Long id);

    void deleteAllStates();
}
