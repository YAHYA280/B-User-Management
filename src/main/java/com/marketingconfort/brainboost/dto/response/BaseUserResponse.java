package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost.dto.request.AddressRequest;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BaseUserResponse {
    private long id;
    private String lastName;
    private String firstName;
    private String email;
    private LocalDate birthDate;
    private UserStatus status;
    private String phoneNumber;
    private String photo;
    private AddressResponse primaryAddress;
    private AddressResponse secondaryAddress;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private UserType userType;
}
