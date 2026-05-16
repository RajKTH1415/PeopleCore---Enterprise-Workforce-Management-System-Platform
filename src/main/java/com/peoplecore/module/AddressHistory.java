package com.peoplecore.module;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
        name = "address_history",
        indexes = {
                @Index(name = "idx_address_history_action", columnList = "action"),
                @Index(name = "idx_address_history_address", columnList = "address_id"),
                @Index(name = "idx_address_history_changed_at", columnList = "changed_at"),
                @Index(
                        name = "idx_address_history_employee_changed",
                        columnList = "employee_id, changed_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "address_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_address_history_address")
    )
    private EmployeeAddress address;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_address_history_employee")
    )
    private Employee employee;

    @Column(name = "action", nullable = false, length = 20)
    private String action;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private Map<String, Object> oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private Map<String, Object> newValue;


    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", length = 50)
    private String changedBy;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;


    @PrePersist
    public void prePersist() {

        this.changedAt = LocalDateTime.now();

        if (this.changedBy == null) {
            this.changedBy = "SYSTEM";
        }
    }
}