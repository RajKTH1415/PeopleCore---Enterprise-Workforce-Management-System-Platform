package com.peoplecore.service;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.EmployeeDashboardResponse;
import com.peoplecore.dto.response.EmployeeHierarchyResponse;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.enums.Status;
import java.util.List;
import java.util.Map;

public interface EmployeeService {



    EmployeeDashboardResponse getEmployeeDashboard();

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(String employeeId , UpdateEmployeeRequest updateEmployeeRequest);

    EmployeeResponse  getEmployeeById(String employeeId , String include);


    EmployeeResponse deleteEmployeeById(String employeeId);

    PageResponse<EmployeeResponse> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction,
            Status status,
            String department,
            String designation,
            String managerId,
            String search
    );
    EmployeeResponse getManager(String id);


    List<EmployeeResponse.Subordinate> getSubordinates(String employeeId);


    List<EmployeeResponse.Subordinate> getAllSubordinates(String employeeId);

    EmployeeHierarchyResponse getEmployeeHierarchy(String employeeId);

    EmployeeResponse onboardEmployee(EmployeeOnboardRequest request);

    EmployeeResponse startProbation(String employeeId);

    EmployeeResponse confirmEmployee(String employeeId);

    EmployeeResponse startNoticePeriod(String employeeId, NoticePeriodRequest request);

    EmployeeResponse exitEmployee(String employeeId);

    EmployeeResponse terminateEmployee(String employeeId, TerminationRequest request);

    Map<String, Object> getEmployeeLifecycleHistory(
            String employeeId,
            int page,
            int size,
            String status,
            String startDate,
            String endDate,
            String sortBy,
            String direction
    );
}