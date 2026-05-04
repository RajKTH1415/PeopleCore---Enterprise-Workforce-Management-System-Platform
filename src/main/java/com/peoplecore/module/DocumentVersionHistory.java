package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_version_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → employee_documents.id
    @Column(name = "document_ref_id", nullable = false)
    private Long documentRefId;

    @Column(name = "document_id", nullable = false, length = 50)
    private String documentId;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "version_comment")
    private String versionComment;

    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
