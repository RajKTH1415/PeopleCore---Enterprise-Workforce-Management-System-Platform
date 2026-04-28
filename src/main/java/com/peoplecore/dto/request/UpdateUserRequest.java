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
public class UpdateUserRequest {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String mobileNumber;
    private Set<RoleName> roles;
}
