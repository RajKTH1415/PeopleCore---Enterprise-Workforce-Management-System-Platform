package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_access_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentRefId;

    private String documentId;

    private String accessedBy;

    private String accessType;

    private LocalDateTime accessedAt;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;


    private String userAgent;
}