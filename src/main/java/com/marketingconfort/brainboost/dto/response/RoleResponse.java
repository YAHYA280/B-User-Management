package com.marketingconfort.brainboost.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class RoleResponse {
    private long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;
    private LocalDateTime createdAt;
}
