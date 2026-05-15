package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "city_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_city_state",
                        columnNames = {"state_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_city_name", columnList = "name"),
                @Index(name = "idx_city_pin_code", columnList = "pin_code"),
                @Index(name = "idx_city_state_id", columnList = "state_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================
    // State Mapping
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "state_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_city_state")
    )
    private StateMaster state;

    // =========================================
    // City Details
    // =========================================

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    // =========================================
    // Status
    // =========================================

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // =========================================
    // Audit Fields
    // =========================================

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // =========================================
    // Lifecycle Hooks
    // =========================================

    @PrePersist
    public void prePersist() {

        this.createdDate = LocalDateTime.now();

        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";
        }

        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}