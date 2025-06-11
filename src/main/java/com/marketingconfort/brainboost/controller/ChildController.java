package com.marketingconfort.brainboost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketingconfort.brainboost.constants.ApiPaths;
import com.marketingconfort.brainboost.dto.request.ChildRequest;
import com.marketingconfort.brainboost.dto.request.SuspensionRequest;
import com.marketingconfort.brainboost.dto.response.ChildResponse;
import com.marketingconfort.brainboost.dto.response.ChildSummaryResponse;
import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.brainboost.service.ChildService;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.CHILD_URL)
public class ChildController {

    private final ChildService childService;

    @Autowired
    public ChildController(
            ChildService childService) {
        this.childService = childService;
    }

    @PostMapping
    public ResponseEntity<ChildResponse> saveUser(
            @RequestPart String saveUserRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ChildRequest childRequest = objectMapper.readValue(saveUserRequest, ChildRequest.class);

            ChildResponse child = childService.addChild(childRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(child);
    }

    @GetMapping(ApiPaths.GET_BY_ID)
    public ResponseEntity<ChildResponse> getChildById(@PathVariable Long id) throws FunctionalException {
        ChildResponse child = childService.getChildById(id);
        return ResponseEntity.ok(child);
    }
    @PatchMapping(ApiPaths.SOFT_DELETE_URL)
    public ResponseEntity<ChildResponse> softDeleteUser(@PathVariable Long id) throws FunctionalException {
        ChildResponse child = childService.softDeleteChild(id);
        return ResponseEntity.ok(child);
    }
    @PatchMapping(ApiPaths.BLOCK_BY_ID)
    public ResponseEntity<Void> blockChild(@PathVariable Long id, @RequestBody String reason) throws FunctionalException{
        childService.blockChild(id, reason);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.REACTIVATE_BY_ID)
    public ResponseEntity<Void> reactivateChild(@PathVariable Long id) throws FunctionalException{
        childService.reactivateChild(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.SUSPEND_BY_ID)
    public ResponseEntity<Void> suspendChild(@PathVariable Long id,@RequestBody SuspensionRequest request) throws FunctionalException{
        childService.suspendChild(id, request.getReason(), request.getSuspensionEnd());
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.SOFT_DELETE_URL_BY_PARENT)
    public ResponseEntity<List<UserResponse>> softDeleteChildByParentId(@PathVariable Long id) throws FunctionalException {
        List<UserResponse> childResponses = childService.softDeleteChildrenByParentId(id);
        return ResponseEntity.ok(childResponses);
    }
    @PatchMapping(ApiPaths.BLOCK_BY_ID_BY_PARENT)
    public ResponseEntity<List<UserResponse>> blockChildByParentId(@PathVariable Long id) throws FunctionalException{
        List<UserResponse> childResponses = childService.blockChildrenByParentId(id);
        return ResponseEntity.ok(childResponses);
    }
    @PatchMapping(ApiPaths.REACTIVATE_BY_ID_BY_PARENT)
    public ResponseEntity<List<UserResponse>> reactivateChildByParentId(@PathVariable Long id) throws FunctionalException{
        List<UserResponse> childResponses = childService.reactivateChildrenByParentId(id);
        return ResponseEntity.ok(childResponses);
    }
    @PatchMapping(ApiPaths.SUSPEND_BY_ID_BY_PARENT)
    public ResponseEntity<List<UserResponse>> suspendChildByParentId(@PathVariable Long id, @RequestBody LocalDate suspensionEnd) throws FunctionalException{
        List<UserResponse> childResponses = childService.suspendChildrenByParentId(id, suspensionEnd);
        return ResponseEntity.ok(childResponses);
    }
    @PutMapping(ApiPaths.UPDATE_BY_ID)
    public ResponseEntity<ChildResponse> updateChild(
            @PathVariable Long id,
            @RequestPart String childRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ChildRequest userRequest = objectMapper.readValue(childRequest, ChildRequest.class);

        ChildResponse childResponse = childService.updateChild(id, userRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(childResponse);
    }
}
