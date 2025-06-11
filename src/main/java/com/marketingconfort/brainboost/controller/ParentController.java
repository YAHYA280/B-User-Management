package com.marketingconfort.brainboost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketingconfort.brainboost.constants.ApiPaths;
import com.marketingconfort.brainboost.dto.request.ParentRequest;
import com.marketingconfort.brainboost.dto.request.SuspensionRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.brainboost.dto.response.ParentResponse;
import com.marketingconfort.brainboost.dto.response.ParentStripeResponse;
import com.marketingconfort.brainboost.dto.response.ParentSummaryResponse;
import com.marketingconfort.brainboost.service.ParentService;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.PARENT_URL)
public class ParentController {

    private final ParentService parentService;

    @Autowired
    public ParentController(
            ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping
    public ResponseEntity<ParentResponse> saveParent(
            @RequestPart String saveUserRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ParentRequest parentRequest = objectMapper.readValue(saveUserRequest, ParentRequest.class);

        ParentResponse parentResponse = parentService.addParent(parentRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(parentResponse);
    }

    @GetMapping(ApiPaths.SEARCH_PARENT_IDS)
    public ResponseEntity<List<Long>> getParentIdsByName(@RequestParam String name) {
            return ResponseEntity.ok(parentService.getParentIdsByNameLike(name));
    }

    @GetMapping(ApiPaths.GET_BY_ID)
    public ResponseEntity<ParentResponse> getParentById(@PathVariable Long id) throws FunctionalException {
        ParentResponse parent = parentService.getParentById(id);
        return ResponseEntity.ok(parent);
    }
    @GetMapping(ApiPaths.GET_BY_CUSTOMERID)
    public ResponseEntity<ParentStripeResponse> getParentByCustomerId(@PathVariable String customerid) throws FunctionalException {
        ParentStripeResponse parent = parentService.getParentByCustomerId(customerid);
        return ResponseEntity.ok(parent);
    }
    @PatchMapping(ApiPaths.SOFT_DELETE_URL)
    public ResponseEntity<ParentResponse> softDeleteUser(@PathVariable Long id) throws TechnicalException,FunctionalException {
        ParentResponse parent = parentService.softDeleteParent(id);
        return ResponseEntity.ok(parent);
    }
    @PatchMapping(ApiPaths.BLOCK_BY_ID)
    public ResponseEntity<Void> blockParent(@PathVariable Long id, @RequestBody String reason) throws FunctionalException{
        parentService.blockParent(id, reason);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.REACTIVATE_BY_ID)
    public ResponseEntity<Void> reactivateParent(@PathVariable Long id) throws FunctionalException{
        parentService.reactivateParent(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(ApiPaths.SUSPEND_BY_ID)
    public ResponseEntity<Void> suspendParent(@PathVariable Long id, @RequestBody SuspensionRequest request) throws FunctionalException{
        parentService.suspendParent(id, request.getReason(), request.getSuspensionEnd());
        return ResponseEntity.noContent().build();
    }
    @PutMapping(ApiPaths.UPDATE_BY_ID)
    public ResponseEntity<ParentResponse> updateParent(
            @PathVariable Long id,
            @RequestPart String parentRequest,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ParentRequest userRequest = objectMapper.readValue(parentRequest, ParentRequest.class);

        ParentResponse parentResponse = parentService.updateParent(id, userRequest, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(parentResponse);
    }
    @PutMapping(ApiPaths.UPDATE_GATEWAY_CUSTOMERID)
    public ResponseEntity<Void> updateGatewayCustomerId(
            @PathVariable Long id,
            @RequestBody String gatewayCustomerId
    ) throws FunctionalException {
        parentService.updateGatewayCustomerId(id, gatewayCustomerId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(ApiPaths.CURRENT_USER)
    public ResponseEntity<ParentResponse> getCurrentUser(JwtAuthenticationToken jwtToke) throws FunctionalException{
        ParentResponse parent = parentService.currentParent(jwtToke);
        return ResponseEntity.ok(parent);
    }
}
