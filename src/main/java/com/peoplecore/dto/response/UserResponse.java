package com.peoplecore.dto.response;

import com.peoplecore.enums.RoleName;
import com.peoplecore.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;
    private String userID;
    private String userName;
    private String userEmail;
    @JsonIgnoreProperties
    private LocalDateTime passwordChangeDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Status status;
    private String mobileNumber;
    private String city;
    private String state;
    private String country;
    private String cluster;
    private Set<RoleName> roles;
}
