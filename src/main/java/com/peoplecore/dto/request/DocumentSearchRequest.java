package com.peoplecore.dto.request;

import com.peoplecore.enums.DocumentStatus;
import com.peoplecore.enums.DocumentType;
import com.peoplecore.enums.ExpiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchRequest {

    private String q;

    private String employeeId;

    private DocumentType documentType;

    private Boolean verified;

    private DocumentStatus status;

    private ExpiryStatus expiryStatus;

    private LocalDate uploadedFrom;

    private LocalDate uploadedTo;

    private LocalDate expiryFrom;

    private LocalDate expiryTo;

    private Integer page;

    private Integer size;

    private String sortBy;

    private String direction;
}
