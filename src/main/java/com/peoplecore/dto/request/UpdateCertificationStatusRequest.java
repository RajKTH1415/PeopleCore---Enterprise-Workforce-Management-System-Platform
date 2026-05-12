package com.peoplecore.dto.request;
import com.peoplecore.enums.CertificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCertificationStatusRequest {

    @NotNull(message = "Status is required")
    private CertificationStatus status;
}