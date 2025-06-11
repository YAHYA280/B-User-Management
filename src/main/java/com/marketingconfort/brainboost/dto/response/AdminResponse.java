package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost.dto.request.RoleRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminResponse extends BaseUserResponse {
    private List<RoleResponse> roles;
}
