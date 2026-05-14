package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "employee_certification_history")
public class EmployeeCertificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long certificationId;

    private String action;

    @Column(columnDefinition = "json")
    private String previousValue;

    @Column(columnDefinition = "json")
    private String newValue;

    private String changedBy;

    private LocalDateTime changedAt;

    private String remarks;
}