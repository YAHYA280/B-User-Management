package com.marketingconfort.brainboost.controller;

import com.marketingconfort.brainboost.constants.ApiPaths;
import com.marketingconfort.brainboost.dto.request.RoleRequest;
import com.marketingconfort.brainboost.dto.response.RoleResponse;
import com.marketingconfort.brainboost.service.RoleService;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ROLE_URL)
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(
            RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Page<RoleResponse> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String createdAt
    ) {
        return roleService.getAllRoles(page, size, sortBy, sortDirection, name, description, createdAt);
    }

    @PostMapping
    public ResponseEntity<RoleResponse> addRole(@RequestBody RoleRequest request) throws FunctionalException{
        RoleResponse roleResponse = roleService.addRole(request);
        return ResponseEntity.ok(roleResponse);
    }

    @PutMapping(ApiPaths.GET_BY_ID)
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid RoleRequest request
    ) throws FunctionalException {
        RoleResponse updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(updatedRole);
    }

    @GetMapping(ApiPaths.GET_BY_ID)
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) throws FunctionalException{
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping(ApiPaths.GET_BY_NAME)
    public ResponseEntity<RoleResponse> getRoleByName(@PathVariable String name) throws FunctionalException{
        RoleResponse role = roleService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }
    @DeleteMapping(ApiPaths.DELETE_BY_ID)
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) throws FunctionalException{
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(ApiPaths.ROLE_IS_ASSIGNED_URL)
    public ResponseEntity<Boolean> isRoleAssigned(@PathVariable Long id) throws FunctionalException {
        boolean assigned = roleService.isRoleAssigned(id);
        return ResponseEntity.ok(assigned);
    }
}

