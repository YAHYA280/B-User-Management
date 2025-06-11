package com.marketingconfort.brainboost.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UsersResponse {
    private List<UserResponse> users;
    private long total;
    private Map<String, Long> statusCounts;
}
