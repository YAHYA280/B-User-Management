package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.RoleRequest;
import com.marketingconfort.brainboost.dto.response.RoleResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.Role;

import java.util.stream.Collectors;

public class RoleMapper {
    public static Role roleRequestToRole(RoleRequest request) {
        if (request == null) {
            return null;
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return role;
    }

    public static RoleResponse roleToRoleResponse(Role role) {
        if (role == null) {
            return null;
        }
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setCreatedAt(role.getCreatedAt());
        response.setPermissions(role.getPermissions().stream().map(PermissionMapper::PermissionToPermissionResponse).collect(Collectors.toList()));
        return response;
    }

    public static RoleResponse roleToRoleResponseWithoutPermissions(Role role) {
        if (role == null) {
            return null;
        }
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setCreatedAt(role.getCreatedAt());
        return response;
    }


}
