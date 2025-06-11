package com.marketingconfort.brainboost.dto.request;

import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BaseUserRequest {
    private String lastName;
    private String firstName;
    private String email;
    private LocalDate birthDate;
    private UserStatus status;
    private String phoneNumber;
    private String photo;
    private AddressRequest primaryAddress;
    private AddressRequest secondaryAddress;
    private MultipartFile imageFile;
}
