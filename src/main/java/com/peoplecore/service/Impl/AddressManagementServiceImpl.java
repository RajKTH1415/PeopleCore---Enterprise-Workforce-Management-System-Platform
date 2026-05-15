package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.AddressRequest;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.dto.response.GeocodeResponse;
import com.peoplecore.module.AddressHistory;
import com.peoplecore.module.CityMaster;
import com.peoplecore.module.CountryMaster;
import com.peoplecore.module.Employee;
import com.peoplecore.module.EmployeeAddress;
import com.peoplecore.module.StateMaster;
import com.peoplecore.repository.AddressHistoryRepository;
import com.peoplecore.repository.CityRepository;
import com.peoplecore.repository.CountryRepository;
import com.peoplecore.repository.EmployeeAddressRepository;
import com.peoplecore.repository.EmployeeRepository;
import com.peoplecore.repository.StateRepository;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AddressManagementServiceImpl
        implements AddressManagementService {

    private final EmployeeAddressRepository employeeAddressRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressHistoryRepository addressHistoryRepository;
    private final GeocodingService geocodingService;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    @Override
    public AddressResponse addAddress(Long employeeId, AddressRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with id : " + employeeId));
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            employeeAddressRepository
                    .findByEmployeeIdAndIsPrimaryTrue(employeeId)
                    .ifPresent(existing -> {

                        existing.setIsPrimary(false);

                        employeeAddressRepository.save(existing);
                    });
        }

        CountryMaster country =
                countryRepository
                        .findByNameIgnoreCase(request.getCountry())
                        .orElse(null);

        StateMaster state = null;

        if (country != null) {

            state = stateRepository
                    .findByNameIgnoreCaseAndCountryId(
                            request.getState(),
                            country.getId()
                    )
                    .orElse(null);
        }

        CityMaster city = null;

        if (state != null) {

            city = cityRepository
                    .findByNameIgnoreCaseAndStateId(
                            request.getCity(),
                            state.getId()
                    )
                    .orElse(null);
        }

        EmployeeAddress address = EmployeeAddress.builder()
                .employee(employee)
                .addressType(request.getAddressType())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pincode(request.getPincode())
                .cityMaster(city)
                .stateMaster(state)
                .countryMaster(country)
                .isPrimary(
                        request.getIsPrimary() != null
                                ? request.getIsPrimary()
                                : false
                )
                .isActive(true)
                .isDeleted(false)
                .isVerified(false)
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .residenceType(request.getResidenceType())
                .staySince(request.getStaySince())
                .notes(request.getNotes())
                .build();

        String fullAddress = String.join(
                ", ",
                request.getAddressLine1(),
                request.getCity(),
                request.getState(),
                request.getPincode(),
                request.getCountry()
        );

        GeocodeResponse geocode =
                geocodingService.geocodeAddress(fullAddress);

        if (geocode != null) {

            address.setLatitude(geocode.getLatitude());

            address.setLongitude(geocode.getLongitude());

            address.setGooglePlaceId(geocode.getPlaceId());
        }

        EmployeeAddress savedAddress =
                employeeAddressRepository.save(address);

        saveAddressHistory(savedAddress, "CREATE");

        return mapToResponse(savedAddress);
    }

    private void saveAddressHistory(EmployeeAddress address,
                                    String action) {

        try {

            Map<String, Object> data = new HashMap<>();

            data.put("addressType", address.getAddressType());
            data.put("addressLine1", address.getAddressLine1());
            data.put("addressLine2", address.getAddressLine2());
            data.put("landmark", address.getLandmark());
            data.put("city", address.getCity());
            data.put("state", address.getState());
            data.put("pincode", address.getPincode());
            data.put("country", address.getCountry());
            data.put("isPrimary", address.getIsPrimary());

            AddressHistory history = AddressHistory.builder()
                    .address(address)
                    .employee(address.getEmployee())
                    .action(action)
                    .newValue(data)
                    .changedAt(LocalDateTime.now())
                    .changedBy("SYSTEM")
                    .remarks(action + " address")
                    .build();

            addressHistoryRepository.save(history);

        } catch (Exception ex) {

            log.error("Error while saving address history", ex);
        }
    }

    private AddressResponse mapToResponse(EmployeeAddress address) {

        return AddressResponse.builder()
                .id(address.getId())
                .employeeId(address.getEmployee().getId())
                .employeeName(
                        address.getEmployee().getFirstName()
                                + " "
                                + address.getEmployee().getLastName()
                )
                .employeeCode(address.getEmployee().getEmployeeId())
                .addressType(address.getAddressType())
                .addressTypeDisplay(
                        address.getAddressType() != null
                                ? address.getAddressType()
                                .replace("_", " ")
                                : null
                )
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .cityId(
                        address.getCityMaster() != null
                                ? address.getCityMaster().getId()
                                : null
                )
                .state(address.getState())
                .stateId(
                        address.getStateMaster() != null
                                ? address.getStateMaster().getId()
                                : null
                )
                .pincode(address.getPincode())
                .country(address.getCountry())
                .countryId(
                        address.getCountryMaster() != null
                                ? address.getCountryMaster().getId()
                                : null
                )
                .fullAddress(buildFormattedAddress(address))
                .formattedAddress(buildFormattedAddress(address))
                .latitude(
                        address.getLatitude() != null
                                ? address.getLatitude().doubleValue()
                                : null
                )
                .longitude(
                        address.getLongitude() != null
                                ? address.getLongitude().doubleValue()
                                : null
                )
                .googlePlaceId(address.getGooglePlaceId())
                .isVerified(address.getIsVerified())
                .verificationStatus(
                        Boolean.TRUE.equals(address.getIsVerified())
                                ? "VERIFIED"
                                : "PENDING"
                )
                .verifiedDate(address.getVerifiedDate())
                .verifiedByName(
                        address.getVerifiedBy() != null
                                ? address.getVerifiedBy().getFirstName()
                                + " "
                                + address.getVerifiedBy().getLastName()
                                : null
                )
                .isPrimary(address.getIsPrimary())
                .isActive(address.getIsActive())
                .effectiveFrom(address.getEffectiveFrom())
                .effectiveTo(address.getEffectiveTo())
                .residenceType(address.getResidenceType())
                .staySince(address.getStaySince())
                .stayDurationMonths(
                        calculateStayDuration(address.getStaySince())
                )
                .notes(address.getNotes())
                .createdDate(address.getCreatedDate())
                .createdBy(address.getCreatedBy())
                .updatedDate(address.getUpdatedDate())
                .updatedBy(address.getUpdatedBy())
                .build();
    }

    private String buildFormattedAddress(EmployeeAddress address) {

        StringBuilder sb = new StringBuilder();

        if (address.getAddressLine1() != null) {
            sb.append(address.getAddressLine1()).append(", ");
        }

        if (address.getAddressLine2() != null) {
            sb.append(address.getAddressLine2()).append(", ");
        }

        if (address.getLandmark() != null) {
            sb.append(address.getLandmark()).append(", ");
        }

        if (address.getCity() != null) {
            sb.append(address.getCity()).append(", ");
        }

        if (address.getState() != null) {
            sb.append(address.getState()).append(", ");
        }

        if (address.getPincode() != null) {
            sb.append(address.getPincode()).append(", ");
        }

        if (address.getCountry() != null) {
            sb.append(address.getCountry());
        }

        return sb.toString().replaceAll(", $", "");
    }

    private Integer calculateStayDuration(LocalDate staySince) {

        if (staySince == null) {
            return null;
        }

        return (int) ChronoUnit.MONTHS
                .between(staySince, LocalDate.now());
    }
}