package com.peoplecore.dto.request;

import com.peoplecore.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserRequest {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String mobileNumber;
    private String city;
    private String country;
    private String state;
    private String cluster;
    private Set<RoleName> roles;
}
