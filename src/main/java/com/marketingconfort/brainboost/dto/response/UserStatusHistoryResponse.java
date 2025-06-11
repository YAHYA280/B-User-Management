package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserStatusHistoryResponse {
    private LocalDateTime createdAt;
    private UserStatus status;
    private LocalDate statusEnd;
    private String reason;
}
