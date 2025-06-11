package com.marketingconfort.brainboost.controller;

import com.marketingconfort.brainboost.constants.ApiPaths;
import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.brainboost.dto.response.UsersResponse;
import com.marketingconfort.brainboost.service.AdminService;
import com.marketingconfort.brainboost.service.UserService;
import com.marketingconfort.starter.core.dtos.requests.AuthenticationRequest;
import com.marketingconfort.starter.core.dtos.responses.AuthenticationResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.USER_URL)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String nameSearch,
            @RequestParam(required = false) String emailSearch,
            @RequestParam(required = false) String statusSearch,
            @RequestParam(required = false) String dateSearch,
            @RequestParam(required = false) String lastLoginSearch,
            @RequestParam(required = false) String roleSearch
    ) {
        UsersResponse result = userService.getAllUsers(page, size, sortBy, sortDirection,
                nameSearch, emailSearch, statusSearch, dateSearch, lastLoginSearch, roleSearch);
        return ResponseEntity.ok(result);
    }
    @PostMapping(ApiPaths.ADMIN_LOGIN)
    public ResponseEntity<AuthenticationResponse> loginBo(@RequestBody AuthenticationRequest request)
            throws TechnicalException, FunctionalException {
        return ResponseEntity.ok(userService.authenticateBo(request));
    }
    @PostMapping(ApiPaths.PARENT_LOGIN)
    public ResponseEntity<AuthenticationResponse> loginAp(@RequestBody AuthenticationRequest request)
            throws TechnicalException, FunctionalException {
        return ResponseEntity.ok(userService.authenticateAp(request));
    }
}
