package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.EmploymentStatus;
import com.peoplecore.enums.Status;
import com.peoplecore.module.*;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import com.peoplecore.repository.EmployeeLifecycleHistoryRepository;
import com.peoplecore.repository.EmployeeRepository;
import com.peoplecore.repository.UserRepository;
import com.peoplecore.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeCertificationsRepository employeeCertificationRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeLifecycleHistoryRepository employeeLifecycleHistoryRepository;

    public EmployeeServiceImpl(EmployeeCertificationsRepository employeeCertificationsRepository, EmployeeCertificationsRepository employeeCertificationRepository, EmployeeRepository employeeRepository, UserRepository userRepository, EmployeeLifecycleHistoryRepository employeeLifecycleHistoryRepository) {
        this.employeeCertificationRepository = employeeCertificationRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.employeeLifecycleHistoryRepository = employeeLifecycleHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDashboardResponse getEmployeeDashboard() {

        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);

        return EmployeeDashboardResponse.builder()
                .totalEmployees(
                        employeeRepository.countByIsDeletedFalse())
                .active(employeeRepository.countByEmploymentStatusAndIsDeletedFalse(EmploymentStatus.ONBOARDED))

                .onProbation(employeeRepository.countByEmploymentStatusAndIsDeletedFalse(EmploymentStatus.PROBATION))

                .onNotice(employeeRepository.countByEmploymentStatusAndIsDeletedFalse(EmploymentStatus.NOTICE_PERIOD))

                .terminated(employeeRepository.countByEmploymentStatusAndIsDeletedFalse(EmploymentStatus.TERMINATED))
                .expiringCertifications(
                        employeeCertificationRepository
                                .countByExpiryDateBetweenAndIsDeletedFalse(
                                        today,
                                        next30Days))
                .build();
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee already exists");
        }
        Employee employee = new Employee();
        employee.setEmployeeId(request.getEmployeeId());
        employee.setUserId(request.getUserId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setMobileNumber(request.getMobile());
        employee.setDesignation(request.getDesignation());
        employee.setDepartment(request.getDepartment());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setStatus(request.getStatus());

        if (request.getEmployeeManagerId() != null && !request.getEmployeeManagerId().isBlank()) {
            Employee manager = employeeRepository.findByEmployeeId(request.getEmployeeManagerId())
                    .orElseThrow(() -> new RuntimeException("Employee Manager not found"));
            employee.setManager(manager);
        }
        employee.setCreatedDate(LocalDateTime.now());
        employee.setUpdatedDate(LocalDateTime.now());
        employee.setCreatedBy("SYSTEM");
        employee.setUpdatedBy("SYSTEM");

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeId(request.getEmployeeId());

        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeResponse savedEmployeeResponse = new EmployeeResponse();
        savedEmployeeResponse.setId(savedEmployee.getId());
        savedEmployeeResponse.setEmployeeId(savedEmployee.getEmployeeId());
        //savedEmployeeResponse.setUserId(savedEmployee.getUserId());
        savedEmployeeResponse.setFirstName(savedEmployee.getFirstName());
        savedEmployeeResponse.setLastName(savedEmployee.getLastName());
        savedEmployeeResponse.setEmail(savedEmployee.getEmail());
        savedEmployeeResponse.setMobile(savedEmployee.getMobileNumber());
        savedEmployeeResponse.setDesignation(savedEmployee.getDesignation());
        savedEmployeeResponse.setDepartment(savedEmployee.getDepartment());
        savedEmployeeResponse.setStatus(savedEmployee.getStatus());
        savedEmployeeResponse.setJoiningDate(savedEmployee.getJoiningDate());
        savedEmployeeResponse.setCreatedDate(savedEmployee.getCreatedDate());
        savedEmployeeResponse.setUpdatedDate(savedEmployee.getUpdatedDate());
        savedEmployeeResponse.setCreatedBy(savedEmployee.getCreatedBy());
        savedEmployeeResponse.setUpdatedBy(savedEmployee.getUpdatedBy());
        savedEmployeeResponse.setSubordinates(Collections.emptyList());

        if (savedEmployee.getManager() != null) {

            savedEmployeeResponse.setManager(
                    new EmployeeResponse.Manager(
                            savedEmployee.getManager().getId(),
                            savedEmployee.getManager().getEmployeeId(),
                            savedEmployee.getManager().getFirstName() + " " + savedEmployee.getManager().getLastName()
                    )
            );
        }
        if (savedEmployee.getUserId() != null) {
            User userEntity = userRepository.findById(savedEmployee.getUserId()).orElse(null);

            String roles = userEntity.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.joining(","));

            savedEmployeeResponse.setUser(
                    new EmployeeResponse.User(
                            userEntity.getId(),
                            userEntity.getUserName(),
                            roles,
                            userEntity.getStatus().name()
                    )
            );
        }

        return savedEmployeeResponse;

    }

    @Override
    public EmployeeResponse updateEmployee(String employeeId, UpdateEmployeeRequest updateEmployeeRequest) {

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID :" + employeeId));

        if (updateEmployeeRequest.getFirstName() != null) {
            employee.setFirstName(updateEmployeeRequest.getFirstName());
        }
        if (updateEmployeeRequest.getLastName() != null) {
            employee.setLastName(updateEmployeeRequest.getLastName());
        }
        if (updateEmployeeRequest.getEmail() != null) {
            employee.setEmail(updateEmployeeRequest.getEmail());
        }
        if (updateEmployeeRequest.getMobile() != null) {
            employee.setMobileNumber(updateEmployeeRequest.getMobile());
        }
        if (updateEmployeeRequest.getDesignation() != null) {
            employee.setDesignation(updateEmployeeRequest.getDesignation());
        }
        if (updateEmployeeRequest.getDepartment() != null) {
            employee.setDepartment(updateEmployeeRequest.getDepartment());
        }
        if (updateEmployeeRequest.getJoiningDate() != null) {
            employee.setJoiningDate(updateEmployeeRequest.getJoiningDate());
        }
        if (updateEmployeeRequest.getStatus() != null) {
            employee.setStatus(updateEmployeeRequest.getStatus());
        }
        if (updateEmployeeRequest.getEmployeeManagerId() != null) {
            if (employeeId.equals(updateEmployeeRequest.getEmployeeManagerId())) {
                throw new RuntimeException("Employee Manager Id is the same");
            }
            Employee manager = employeeRepository.findByEmployeeId(updateEmployeeRequest.getEmployeeManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID :" + updateEmployeeRequest.getEmployeeManagerId()));
            employee.setManager(manager);
        }


        employee.setUpdatedDate(LocalDateTime.now());
        employee.setUpdatedBy("SYSTEM");

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(String employeeId, String include) {

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        EmployeeResponse response = mapToResponse(employee);

        Set<String> includes = parseIncludes(include);

        if (includes.contains("manager") && employee.getManager() != null) {

            response.setManager(new EmployeeResponse.Manager(
                    employee.getManager().getId(),
                    employee.getManager().getEmployeeId(),
                    employee.getManager().getFirstName() + " " + employee.getManager().getLastName()
            ));
        }

        if (includes.contains("subordinates")) {

            List<Employee> subs = employeeRepository.findByManager(employee);

            List<EmployeeResponse.Subordinate> subList = subs.stream()
                    .map(e -> new EmployeeResponse.Subordinate(
                            e.getId(),
                            e.getEmployeeId(),
                            e.getFirstName() + " " + e.getLastName()
                    ))
                    .toList();

            response.setSubordinates(subList);
        }

        if (includes.contains("user") && employee.getUserId() != null) {

            User userEntity = userRepository.findById(employee.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));


            String roles = userEntity.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.joining(","));

            EmployeeResponse.User user = new EmployeeResponse.User(
                    userEntity.getId(),
                    userEntity.getUserName(),
                    roles,
                    userEntity.getStatus().name()
            );

            response.setUser(user);
        }
        return response;
    }

    @Override
    public EmployeeResponse deleteEmployeeById(String employeeId) {

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        if (employee.getStatus() == Status.DELETED) {
            throw new RuntimeException("Employee already deleted with ID: " + employeeId);
        }

        employee.setStatus(Status.INACTIVE);
        employee.setIsDeleted(true);
        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        employee.setUpdatedDate(LocalDateTime.now());
        employee.setUpdatedBy("SYSTEM");

        Employee savedEmployee = employeeRepository.save(employee);


        return EmployeeResponse.builder()
                .employeeId(savedEmployee.getEmployeeId())
                .status(savedEmployee.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String direction, Status status, String department, String designation, String managerId, String search) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, sortBy).and(Sort.by(Sort.Direction.ASC, "employeeId"));

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeePage = employeeRepository.findEmployeesWithFilters(status, department, designation, managerId, search, pageable);
        List<EmployeeResponse> responses = employeePage.getContent()
                .stream()
                .map(emp -> {

                    EmployeeResponse.Manager managerDto = null;
                    if (emp.getManager() != null) {
                        managerDto = new EmployeeResponse.Manager(
                                emp.getManager().getId(),
                                emp.getManager().getEmployeeId(),
                                emp.getManager().getFirstName() + " " + emp.getManager().getLastName()
                        );
                    }

                    List<EmployeeResponse.Subordinate> subordinateDtos = null;
                    if (emp.getSubordinates() != null && !emp.getSubordinates().isEmpty()) {
                        subordinateDtos = emp.getSubordinates().stream()
                                .map(sub -> new EmployeeResponse.Subordinate(
                                        sub.getId(),
                                        sub.getEmployeeId(),
                                        sub.getFirstName() + " " + sub.getLastName()
                                ))
                                .toList();
                    }

                    return EmployeeResponse.builder()
                            .id(emp.getId())
                            .employeeId(emp.getEmployeeId())
                            .firstName(emp.getFirstName())
                            .lastName(emp.getLastName())
                            .email(emp.getEmail())
                            .mobile(emp.getMobileNumber())
                            .designation(emp.getDesignation())
                            .department(emp.getDepartment())
                            .status(emp.getStatus())
                            .joiningDate(emp.getJoiningDate())
                            .createdDate(emp.getCreatedDate())
                            .updatedDate(emp.getUpdatedDate())
                            .createdBy(emp.getCreatedBy())
                            .updatedBy(emp.getUpdatedBy())
                            .manager(managerDto)
                            // .subordinates(Collections.emptyList()) // keep lightweight
                            .subordinates(subordinateDtos != null ? subordinateDtos : Collections.emptyList())
                            .user(null) // avoid heavy fetch
                            .build();
                })
                .toList();

        return PageResponse.<EmployeeResponse>builder()
                .content(responses)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .numberOfElements(employeePage.getNumberOfElements())
                .first(employeePage.isFirst())
                .last(employeePage.isLast())
                .hasNext(employeePage.hasNext())
                .hasPrevious(employeePage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }

    @Override
    public EmployeeResponse getManager(String id) {

        Employee employee = employeeRepository.findEmployeeWithManager(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Employee manager = employee.getManager();

        EmployeeResponse.Manager managerDto = null;

        if (manager != null) {
            managerDto = new EmployeeResponse.Manager(
                    manager.getId(),
                    manager.getEmployeeId(),
                    manager.getFirstName() + " " + manager.getLastName()
            );
        }

        return EmployeeResponse.builder()
                .manager(managerDto)
                .build();
    }

    @Override
    public List<EmployeeResponse.Subordinate> getSubordinates(String employeeId) {

        List<Employee> employees = employeeRepository.findSubordinates(employeeId);

        return employees.stream()
                .map(e -> new EmployeeResponse.Subordinate(
                        e.getId(),
                        e.getEmployeeId(),
                        e.getFirstName() + " " + e.getLastName()
                ))
                .toList();
    }

    @Override
    public List<EmployeeResponse.Subordinate> getAllSubordinates(String employeeId) {
        Employee root = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        List<EmployeeResponse.Subordinate> result = new ArrayList<>();

        fetchAllSubordinates(root, result);

        return result;
    }

    @Override
    public EmployeeHierarchyResponse getEmployeeHierarchy(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Build downward tree
        EmployeeHierarchyResponse root = buildSubordinateTree(employee);

        // Attach manager chain
        root.setManager(buildManagerChain(employee.getManager()));

        return root;
    }

    @Override
    @Transactional
    public EmployeeResponse onboardEmployee(EmployeeOnboardRequest request) {

        Employee employee = new Employee();

        int randomNum = 1000 + new Random().nextInt(9000);
        String employeeId = "MSGQ-EMP-" + randomNum;
        employee.setEmployeeId(employeeId);

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setMobileNumber(request.getMobile());
        employee.setDesignation(request.getDesignation());
        employee.setDepartment(request.getDepartment());
        employee.setUserId(request.getUserId());
        LocalDate joiningDate = request.getJoiningDate() != null
                ? request.getJoiningDate()
                : LocalDate.now();
        employee.setJoiningDate(joiningDate);

        if (request.getManagerEmployeeId() != null && !request.getManagerEmployeeId().isBlank()) {
            Employee manager = employeeRepository.findByEmployeeId(request.getManagerEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Manager not found: " + request.getManagerEmployeeId()));
            employee.setManager(manager);
        }


        employee.setEmploymentStatus(EmploymentStatus.ONBOARDED);
        employee.setStatus(Status.ACTIVE);


        employee.setProbationEndDate(joiningDate.plusDays(90));


        employee.setCreatedDate(LocalDateTime.now());
        employee.setUpdatedDate(LocalDateTime.now());
        employee.setCreatedBy("SYSTEM");
        employee.setUpdatedBy("SYSTEM");


        Employee savedEmployee = employeeRepository.save(employee);


        EmployeeLifecycleHistory history = new EmployeeLifecycleHistory();
//        history.setEmployeeId(savedEmployee.getId());
        history.setEmployee(employee);
        history.setOldStatus(null);
        history.setNewStatus(EmploymentStatus.ONBOARDED.name());
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");
        history.setRemarks("Employee onboarded");

        employeeLifecycleHistoryRepository.save(history);


        return EmployeeResponse.builder()
                .id(savedEmployee.getId())
                .employeeId(savedEmployee.getEmployeeId())
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .email(savedEmployee.getEmail())
                .mobile(savedEmployee.getMobileNumber())
                .designation(savedEmployee.getDesignation())
                .department(savedEmployee.getDepartment())
                .status(savedEmployee.getStatus())
                .employmentStatus(savedEmployee.getEmploymentStatus())
                .joiningDate(savedEmployee.getJoiningDate())
                .createdDate(savedEmployee.getCreatedDate())
                .updatedDate(savedEmployee.getUpdatedDate())
                .createdBy(savedEmployee.getCreatedBy())
                .updatedBy(savedEmployee.getUpdatedBy())
                .build();
    }

    @Override
    public EmployeeResponse startProbation(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID :" + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.ONBOARDED) {
            throw new RuntimeException("Only ONBOARDED employee can move to PROBATION");
        }
        EmploymentStatus oldEmploymentStatus = employee.getEmploymentStatus();
        employee.setEmploymentStatus(EmploymentStatus.PROBATION);

        employee.setProbationEndDate(LocalDate.now().plusDays(90));
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeLifecycleHistory employeeLifecycleHistory = new EmployeeLifecycleHistory();
//        employeeLifecycleHistory.setEmployee(savedEmployee.getId());
        employeeLifecycleHistory.setEmployee(savedEmployee);
        employeeLifecycleHistory.setOldStatus(oldEmploymentStatus.name());
        employeeLifecycleHistory.setNewStatus(EmploymentStatus.PROBATION.name());
        employeeLifecycleHistory.setChangedAt(LocalDateTime.now());
        employeeLifecycleHistory.setChangedBy("SYSTEM");
        employeeLifecycleHistory.setRemarks("Employee moved to PROBATION");

        employeeLifecycleHistoryRepository.save(employeeLifecycleHistory);

        return EmployeeResponse.builder()
                .id(savedEmployee.getId())
                .employeeId(savedEmployee.getEmployeeId())
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .email(savedEmployee.getEmail())
                .mobile(savedEmployee.getMobileNumber())
                .designation(savedEmployee.getDesignation())
                .department(savedEmployee.getDepartment())
                .status(savedEmployee.getStatus())
                .joiningDate(savedEmployee.getJoiningDate())
                .employmentStatus(EmploymentStatus.PROBATION)
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();

    }

    @Override
    @Transactional
    public EmployeeResponse confirmEmployee(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID :" + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.PROBATION) {
            throw new RuntimeException(
                    "Only employees in PROBATION can be confirmed. Current status: "
                            + employee.getEmploymentStatus());
        }
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setConfirmationDate(LocalDate.now());

        employee.setUpdatedDate(LocalDateTime.now());
        employee.setUpdatedBy("SYSTEM");

        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeLifecycleHistory history = new EmployeeLifecycleHistory();
//        history.setEmployeeId(savedEmployee.getId());
        history.setEmployee(savedEmployee);
        history.setOldStatus(EmploymentStatus.PROBATION.name());
        history.setNewStatus(EmploymentStatus.ACTIVE.name());
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");
        history.setRemarks("Employee confirmed after probation");

        employeeLifecycleHistoryRepository.save(history);


        return EmployeeResponse.builder()
                .id(savedEmployee.getId())
                .employeeId(savedEmployee.getEmployeeId())
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .email(savedEmployee.getEmail())
                .mobile(savedEmployee.getMobileNumber())
                .joiningDate(savedEmployee.getJoiningDate())
                .status(savedEmployee.getStatus())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .department(savedEmployee.getDepartment())
                .designation(savedEmployee.getDesignation())
                .createdDate(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedDate(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();
    }

    @Override
    public EmployeeResponse startNoticePeriod(String employeeId, NoticePeriodRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.ACTIVE) {
            throw new RuntimeException(
                    "Only ACTIVE employees can be moved to NOTICE_PERIOD. Current status: "
                            + employee.getEmploymentStatus());
        }
        if (request.getNoticeDays() == null || request.getNoticeDays() <= 0) {
            throw new RuntimeException("Notice days must be greater than 0");
        }
        LocalDate noticeStartDate = LocalDate.now();
        LocalDate exitDate = noticeStartDate.plusDays(request.getNoticeDays());

        employee.setNoticeStartDate(noticeStartDate);
        employee.setExitDate(exitDate);

        employee.setEmploymentStatus(EmploymentStatus.NOTICE_PERIOD);

        employee.setUpdatedDate(LocalDateTime.now());
        employee.setUpdatedBy("SYSTEM");

        Employee updatedEmployee = employeeRepository.save(employee);

        EmployeeLifecycleHistory history = new EmployeeLifecycleHistory();
//        history.setEmployeeId(updatedEmployee.getId());
        history.setEmployee(updatedEmployee);
        history.setOldStatus(EmploymentStatus.ACTIVE.name());
        history.setNewStatus(EmploymentStatus.NOTICE_PERIOD.name());
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");
        history.setRemarks("Employee moved to notice period. Reason: " + request.getReason());

        employeeLifecycleHistoryRepository.save(history);


        return EmployeeResponse.builder()
                .id(updatedEmployee.getId())
                .employeeId(updatedEmployee.getEmployeeId())
                .firstName(updatedEmployee.getFirstName())
                .lastName(updatedEmployee.getLastName())
                .email(updatedEmployee.getEmail())
                .mobile(updatedEmployee.getMobileNumber())
                .designation(updatedEmployee.getDesignation())
                .department(updatedEmployee.getDepartment())
                .status(updatedEmployee.getStatus())
                .joiningDate(updatedEmployee.getJoiningDate())
                .noticeStartDate(employee.getNoticeStartDate())
                .exitDate(employee.getExitDate())
                .employmentStatus(EmploymentStatus.NOTICE_PERIOD)
                .updatedBy("SYSTEM")
                .updatedDate(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

    }

    @Override
    @Transactional
    public EmployeeResponse exitEmployee(String employeeId) {

        // 1. Fetch employee
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // 2. Validate status
        if (employee.getEmploymentStatus() != EmploymentStatus.NOTICE_PERIOD) {
            throw new RuntimeException(
                    "Only employees in NOTICE_PERIOD can be exited. Current status: "
                            + employee.getEmploymentStatus());
        }

        // 3. Validate exit date
        if (employee.getExitDate() == null) {
            throw new RuntimeException("Exit date is not set for this employee");
        }

        if (LocalDate.now().isBefore(employee.getExitDate())) {
            throw new RuntimeException(
                    "Employee cannot be exited before exit date: " + employee.getExitDate());
        }

        employee.setEmploymentStatus(EmploymentStatus.EXITED);

        employee.setUpdatedDate(LocalDateTime.now());
        employee.setUpdatedBy("SYSTEM");

        Employee updatedEmployee = employeeRepository.save(employee);

        EmployeeLifecycleHistory history = new EmployeeLifecycleHistory();
//        history.setEmployeeId(updatedEmployee.getId());
        history.setEmployee(updatedEmployee);
        history.setOldStatus(EmploymentStatus.NOTICE_PERIOD.name());
        history.setNewStatus(EmploymentStatus.EXITED.name());
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");
        history.setRemarks("Employee exited successfully");

        employeeLifecycleHistoryRepository.save(history);

        // 7. Response
        return EmployeeResponse.builder()
                .id(updatedEmployee.getId())
                .employeeId(updatedEmployee.getEmployeeId())
                .firstName(updatedEmployee.getFirstName())
                .lastName(updatedEmployee.getLastName())
                .email(updatedEmployee.getEmail())
                .mobile(updatedEmployee.getMobileNumber())
                .designation(updatedEmployee.getDesignation())
                .department(updatedEmployee.getDepartment())
                .status(updatedEmployee.getStatus())

                //  Use actual values
                .employmentStatus(updatedEmployee.getEmploymentStatus())
                .joiningDate(updatedEmployee.getJoiningDate())
                .noticeStartDate(updatedEmployee.getNoticeStartDate())
                .exitDate(updatedEmployee.getExitDate())

                .createdDate(updatedEmployee.getCreatedDate())
                .updatedDate(updatedEmployee.getUpdatedDate())
                .createdBy(updatedEmployee.getCreatedBy())
                .updatedBy(updatedEmployee.getUpdatedBy())

                .build();
    }

    @Override
    @Transactional
    public EmployeeResponse terminateEmployee(String employeeId, TerminationRequest request) {

        // 1. Fetch employee
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new RuntimeException("Employee already terminated");
        }

        // 2. Store old status
        String oldStatus = employee.getEmploymentStatus().name();

        // 3. Update fields
        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        employee.setExitDate(LocalDate.now());
        employee.setTerminationReason(request.getReason());

        employee.setUpdatedBy("SYSTEM");
        employee.setUpdatedDate(LocalDateTime.now());

        // 4. Save employee
        Employee updatedEmployee = employeeRepository.save(employee);

        // 5. Save lifecycle history
        EmployeeLifecycleHistory history = new EmployeeLifecycleHistory();
//        history.setEmployeeId(updatedEmployee.getId());
        history.setEmployee(updatedEmployee);
        history.setOldStatus(oldStatus);
        history.setNewStatus(EmploymentStatus.TERMINATED.name());
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");
        history.setRemarks("Employee terminated: " + request.getReason());

        employeeLifecycleHistoryRepository.save(history);

        // 6. Return response (USE DB VALUES, not LocalDate.now blindly)
        return EmployeeResponse.builder()
                .id(updatedEmployee.getId())
                .employeeId(updatedEmployee.getEmployeeId())
                .firstName(updatedEmployee.getFirstName())
                .lastName(updatedEmployee.getLastName())
                .email(updatedEmployee.getEmail())
                .mobile(updatedEmployee.getMobileNumber())
                .designation(updatedEmployee.getDesignation())
                .department(updatedEmployee.getDepartment())
                .status(updatedEmployee.getStatus())
                .employmentStatus(updatedEmployee.getEmploymentStatus())
                .joiningDate(updatedEmployee.getJoiningDate())
                .exitDate(updatedEmployee.getExitDate())
                .terminationReason(updatedEmployee.getTerminationReason())
                .createdDate(updatedEmployee.getCreatedDate())
                .createdBy(updatedEmployee.getCreatedBy())
                .updatedDate(updatedEmployee.getUpdatedDate())
                .updatedBy(updatedEmployee.getUpdatedBy())
                .build();
    }

    @Override
    public Map<String, Object> getEmployeeLifecycleHistory(
            String employeeId,
            int page,
            int size,
            String status,
            String startDate,
            String endDate,
            String sortBy,
            String direction) {

        // 1. Validate employee
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // 2. Sorting
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeLifecycleHistory> historyPage;

        // 3. Apply filters
        if (status != null && startDate != null && endDate != null) {

            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

            historyPage = employeeLifecycleHistoryRepository
                    .findByEmployee_IdAndNewStatusAndChangedAtBetween(
                            employee.getId(), status, start, end, pageable);

        } else {
            historyPage = employeeLifecycleHistoryRepository
                    .findByEmployee_Id(employee.getId(), pageable);
        }

        // 4. Map DTO
        List<EmployeeLifecycleHistoryResponse> content = historyPage.getContent()
                .stream()
                .map(h -> EmployeeLifecycleHistoryResponse.builder()
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .changedAt(h.getChangedAt())
                        .changedBy(h.getChangedBy())
                        .remarks(h.getRemarks())
                        .build())
                .toList();

        // 5. Build PageResponse
        PageResponse<EmployeeLifecycleHistoryResponse> pageResponse =
                PageResponse.<EmployeeLifecycleHistoryResponse>builder()
                        .content(content)
                        .page(historyPage.getNumber())
                        .size(historyPage.getSize())
                        .totalElements(historyPage.getTotalElements())
                        .totalPages(historyPage.getTotalPages())
                        .numberOfElements(historyPage.getNumberOfElements())
                        .first(historyPage.isFirst())
                        .last(historyPage.isLast())
                        .hasNext(historyPage.hasNext())
                        .hasPrevious(historyPage.hasPrevious())
                        .sortBy(sortBy)
                        .direction(direction)
                        .build();

        // 6. Timeline (UI Friendly)
        List<String> timeline = historyPage.getContent()
                .stream()
                .map(EmployeeLifecycleHistory::getNewStatus)
                .toList();

        // 7. Final Response
        Map<String, Object> response = new HashMap<>();
        response.put("history", pageResponse);
        response.put("timeline", timeline);
        response.put("currentStatus", employee.getEmploymentStatus());

        return response;
    }

    private EmployeeHierarchyResponse buildManagerChain(Employee manager) {

        if (manager == null) return null;

        EmployeeHierarchyResponse node = mapToHierarchy(manager);

        node.setManager(buildManagerChain(manager.getManager())); // upward recursion

        return node;
    }
    private EmployeeHierarchyResponse buildSubordinateTree(Employee employee) {

        EmployeeHierarchyResponse node = mapToHierarchy(employee);

        List<Employee> subs = employeeRepository.findSubordinates(employee.getEmployeeId());

        List<EmployeeHierarchyResponse> subNodes = subs.stream()
                .map(this::buildSubordinateTree) // recursion
                .toList();

        node.setSubordinates(subNodes);

        return node;
    }
    private EmployeeHierarchyResponse mapToHierarchy(Employee emp) {
        return EmployeeHierarchyResponse.builder()
                .id(emp.getId())
                .employeeId(emp.getEmployeeId())
                .fullName(emp.getFirstName() + " " + emp.getLastName())
                .build();
    }

    private void fetchAllSubordinates(Employee manager,
                                      List<EmployeeResponse.Subordinate> result) {

        if (manager.getSubordinates() == null || manager.getSubordinates().isEmpty()) {
            return;
        }

        for (Employee sub : manager.getSubordinates()) {

            result.add(new EmployeeResponse.Subordinate(
                    sub.getId(),
                    sub.getEmployeeId(),
                    sub.getFirstName() + " " + sub.getLastName()
            ));

            fetchAllSubordinates(sub, result); // recursion
        }
    }

    private Set<String> parseIncludes(String include) {
        if (include == null || include.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(include.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private EmployeeResponse mapToResponse(Employee emp) {

        EmployeeResponse res = new EmployeeResponse();

        res.setId(emp.getId());
        res.setEmployeeId(emp.getEmployeeId());
       // res.setUserId(emp.getUserId());
        res.setFirstName(emp.getFirstName());
        res.setLastName(emp.getLastName());
        res.setEmail(emp.getEmail());
        res.setMobile(emp.getMobileNumber());
        res.setDesignation(emp.getDesignation());
        res.setDepartment(emp.getDepartment());
        res.setStatus(emp.getStatus());
        res.setJoiningDate(emp.getJoiningDate());
        res.setCreatedDate(LocalDateTime.now());
        res.setUpdatedDate(LocalDateTime.now());
        res.setCreatedBy("SYSTEM");
        res.setUpdatedBy("SYSTEM");

        return res;
    }
}
