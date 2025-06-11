package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleRequest {
    private String name;
    private String description;
    private List<PermissionRequest> permissions;
}
