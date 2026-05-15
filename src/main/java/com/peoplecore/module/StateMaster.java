package com.peoplecore.module;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "state_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_state_country",
                        columnNames = {"country_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_state_country_id", columnList = "country_id"),
                @Index(name = "idx_state_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "country_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_state_country")
    )
    private CountryMaster country;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "capital", length = 100)
    private String capital;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false, length = 50)
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
