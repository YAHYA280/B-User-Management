package com.marketingconfort.brainboost.service;

import com.marketingconfort.brainboost.dto.request.RoleRequest;
import com.marketingconfort.brainboost.dto.response.RoleResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.Role;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    RoleResponse addRole(RoleRequest request) throws FunctionalException;
    RoleResponse updateRole(Long id, RoleRequest request) throws FunctionalException;
    RoleResponse getRoleById(Long id) throws FunctionalException;
    RoleResponse getRoleByName(String name) throws FunctionalException;
    List<Role> getAllRolesByIds(List<Long> roleIds);
    Page<RoleResponse> getAllRoles(int page, int size, String sortBy, String sortDirection,
                                   String name, String description, String createdAt);
    void deleteRole(Long id) throws FunctionalException;
    boolean isRoleAssigned(Long id) throws FunctionalException;
}
