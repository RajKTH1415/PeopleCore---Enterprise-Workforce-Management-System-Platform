package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_lifecycle_history")
@Data
public class EmployeeLifecycleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


@ManyToOne
@JoinColumn(name = "employee_id")
private Employee employee;

    private String oldStatus;
    private String newStatus;

    private LocalDateTime changedAt;
    private String changedBy;

    private String remarks;
}
