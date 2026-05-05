package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "issuer"}))
@Getter
@Setter
public class Certification extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String issuer;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
