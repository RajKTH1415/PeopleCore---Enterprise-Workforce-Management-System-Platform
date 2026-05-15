package com.peoplecore.module;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "country_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "country_master_code_key", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "dial_code", length = 10)
    private String dialCode;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

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
