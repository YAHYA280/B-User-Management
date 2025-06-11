package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.PermissionRequest;
import com.marketingconfort.brainboost.dto.response.PermissionResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.Permission;

public class PermissionMapper {
    public static PermissionResponse PermissionToPermissionResponse(Permission rolePermission) {
        if (rolePermission == null) {
            return null;
        }
        PermissionResponse response = new PermissionResponse();
        response.setId(rolePermission.getId());
        response.setPermissionType(rolePermission.getPermissionType());
        response.setCreatedAt(rolePermission.getCreatedAt());
        response.setSubModule(rolePermission.getSubModule());
        return response;
    }

    public static Permission PermissionRequestToPermission(PermissionRequest request) {
        if (request == null) {
            return null;
        }
        Permission rolePermission = new Permission();
        rolePermission.setPermissionType(request.getPermissionType());
        rolePermission.setSubModule(request.getSubModule());
        return rolePermission;
    }
}
