package com.marketingconfort.brainboost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketingconfort.brainboost.constants.ApiPaths;
import com.marketingconfort.brainboost.dto.request.AdminRequest;
import com.marketingconfort.brainboost.dto.request.SuspensionRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.brainboost.service.AdminService;
import com.marketingconfort.brainboost_common.usermanagement.models.Admin;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ADMIN_URL)
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(
            AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<AdminResponse> saveUser(
            @RequestPart String saveUserRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AdminRequest userRequest = objectMapper.readValue(saveUserRequest, AdminRequest.class);

        AdminResponse admin = adminService.addAdmin(userRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    @GetMapping(ApiPaths.GET_BY_ID)
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable Long id) throws FunctionalException {
        AdminResponse admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }
    @PatchMapping(ApiPaths.SOFT_DELETE_URL)
    public ResponseEntity<AdminResponse> softDeleteUser(@PathVariable Long id) throws TechnicalException,FunctionalException {
        AdminResponse admin = adminService.softDeleteAdmin(id);
        return ResponseEntity.ok(admin);
    }
    @PatchMapping(ApiPaths.BLOCK_BY_ID)
    public ResponseEntity<Void> blockAdmin(@PathVariable Long id, @RequestBody String reason) throws FunctionalException{
        adminService.blockAdmin(id, reason);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.REACTIVATE_BY_ID)
    public ResponseEntity<Void> reactivateAdmin(@PathVariable Long id) throws FunctionalException{
        adminService.reactivateAdmin(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.SUSPEND_BY_ID)
    public ResponseEntity<Void> suspendAdmin(@PathVariable Long id,
                                             @RequestBody SuspensionRequest request) throws FunctionalException{
        adminService.suspendAdmin(id, request.getReason(), request.getSuspensionEnd());
        return ResponseEntity.noContent().build();
    }
    @PutMapping(ApiPaths.UPDATE_BY_ID)
    public ResponseEntity<AdminResponse> updateAdmin(
            @PathVariable Long id,
            @RequestPart String adminRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AdminRequest userRequest = objectMapper.readValue(adminRequest, AdminRequest.class);

        AdminResponse admin = adminService.updateAdmin(id, userRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
    @PatchMapping(ApiPaths.UPDATE_ADMIN_ROLE_BY_ID)
    public ResponseEntity<AdminResponse> updateAdminRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds
            ) throws Exception {

        AdminResponse admin = adminService.updateAdminRoles(id, roleIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
    @GetMapping(ApiPaths.CURRENT_USER)
    public ResponseEntity<AdminResponse> getCurrentUser(JwtAuthenticationToken jwtToke) throws FunctionalException{
        AdminResponse admin = adminService.currentAdmin(jwtToke);
        return ResponseEntity.ok(admin);
    }
    @PutMapping(ApiPaths.CURRENT_USER)
    public ResponseEntity<AdminResponse> updateCurrentAdmin(
            JwtAuthenticationToken jwtToken,
            @RequestBody AdminRequest adminRequest) throws FunctionalException{
            AdminResponse updatedAdmin = adminService.updateCurrentAdmin(jwtToken, adminRequest);
            return ResponseEntity.ok(updatedAdmin);
    }
}
