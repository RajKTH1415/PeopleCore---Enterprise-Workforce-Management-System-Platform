package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.EmployeeDashboardResponse;
import com.peoplecore.dto.response.EmployeeHierarchyResponse;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.enums.Status;
import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeManagementController {

    private final EmployeeService employeeService;

    public EmployeeManagementController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@RequestBody EmployeeRequest employeeRequest, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Employee created successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<EmployeeDashboardResponse>>
    getEmployeeDashboard(HttpServletRequest httpServletRequest) {

        EmployeeDashboardResponse response =
                employeeService.getEmployeeDashboard();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee dashboard fetched successfully",httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<EmployeeResponse>> onboardEmployee(@RequestBody EmployeeOnboardRequest employeeOnboardRequest , HttpServletRequest httpServletRequest) {
        EmployeeResponse employeeResponse = employeeService.onboardEmployee(employeeOnboardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Employee onboarded successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @PutMapping("/{employeeId}/start-probation")
    public ResponseEntity<ApiResponse<EmployeeResponse>> startProbation(@PathVariable("employeeId") String employeeId, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.startProbation(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee moved to PROBATION", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @PutMapping("/{employeeId}/confirm")
    public ResponseEntity<ApiResponse<EmployeeResponse>> confirmEmployee(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.confirmEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee confirmed successfully", httpServletRequest.getRequestURI(), response));
    }
    @PutMapping("/{employeeId}/notice")
    public ResponseEntity<ApiResponse<EmployeeResponse>> startNoticePeriod(@PathVariable String employeeId, @RequestBody NoticePeriodRequest noticePeriodRequest , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.startNoticePeriod(employeeId, noticePeriodRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee moved to notice period", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @PutMapping("/{employeeId}/exit")
    public ResponseEntity<ApiResponse<EmployeeResponse>> exitEmployee(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.exitEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),  httpServletRequest.getRequestURI(),"Employee exited successfully",response));
    }
    @PutMapping("/{employeeId}/terminate")
    public ResponseEntity<ApiResponse<EmployeeResponse>> terminateEmployee(@PathVariable String employeeId, @RequestBody TerminationRequest request, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.terminateEmployee(employeeId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI(),"Employee terminated successfully", response));
    }
    @GetMapping("/{employeeId}/lifecycle-history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLifecycleHistory(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "changedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpServletRequest) {
        Map<String, Object> response = employeeService.getEmployeeLifecycleHistory(
                        employeeId, page, size, status, startDate, endDate, sortBy, direction);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee lifecycle history fetched successfully",httpServletRequest.getRequestURI(),response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String search,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeResponse> response = employeeService.getAllEmployees(
                page, size, sortBy, direction, status, department, designation, managerId, search);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "All employees fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/updateEmployee/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable("employeeId") String employeeId , @RequestBody UpdateEmployeeRequest updateEmployeeRequest, HttpServletRequest httpServletRequest){
        EmployeeResponse updatedEmployeeResponse = employeeService.updateEmployee(employeeId, updateEmployeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee updated successfully", httpServletRequest.getRequestURI(), updatedEmployeeResponse));
    }
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable String employeeId ,  @RequestParam(value = "include", required = false) String include , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.getEmployeeById(employeeId , include);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee fetched  successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> softDeleteEmployee(@PathVariable("employeeId") String employeeId , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.deleteEmployeeById(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee deleted successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }

    @GetMapping("/{employeeId}/manager")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getManager(@PathVariable String employeeId, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.getManager(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Manager fetched successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @GetMapping("/{employeeId}/subordinates")
    public ResponseEntity<ApiResponse<List<EmployeeResponse.Subordinate>>> getSubordinates(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        List<EmployeeResponse.Subordinate> subOrdinateResponse = employeeService.getSubordinates(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Subordinates fetched successfully", httpServletRequest.getRequestURI(), subOrdinateResponse));
    }
    @GetMapping("/{employeeId}/subordinates/all")
    public ResponseEntity<ApiResponse<List<EmployeeResponse.Subordinate>>> getAllSubordinates(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        List<EmployeeResponse.Subordinate> subordinates = employeeService.getAllSubordinates(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Subordinates fetched successfully", httpServletRequest.getRequestURI(), subordinates ));
    }
    @GetMapping("/{employeeId}/hierarchy")
    public ResponseEntity<ApiResponse<EmployeeHierarchyResponse>> getHierarchy(@PathVariable("employeeId") String id, HttpServletRequest httpServletRequest) {
        EmployeeHierarchyResponse employeeHierarchyResponse=employeeService.getEmployeeHierarchy(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "GetHierarchy fetch  successfully",httpServletRequest.getRequestURI(),employeeHierarchyResponse));
    }

}
