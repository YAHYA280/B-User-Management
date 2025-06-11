package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class UserResponse {
    private long id;
    private String lastName;
    private String firstName;
    private String email;
    private UserType userType;
    private UserStatus status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
