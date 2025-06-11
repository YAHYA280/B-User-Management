package com.marketingconfort.brainboost.service.impl;

import com.marketingconfort.brainboost.constants.MessageConstants;
import com.marketingconfort.brainboost.dto.request.AdminRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.brainboost.dto.response.UserStatusHistoryResponse;
import com.marketingconfort.brainboost.mapper.AddressMapper;
import com.marketingconfort.brainboost.mapper.AdminMapper;
import com.marketingconfort.brainboost.mapper.RoleMapper;
import com.marketingconfort.brainboost.repository.AdminRepository;
import com.marketingconfort.brainboost.repository.UserStatusHistoryRepository;
import com.marketingconfort.brainboost.service.AdminService;
import com.marketingconfort.brainboost.service.RoleService;
import com.marketingconfort.brainboost.service.UserService;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.models.*;
import com.marketingconfort.starter.core.dtos.requests.*;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import com.marketingconfort.starter.core.services.AuthService;
import com.marketingconfort.starter.core.services.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;
    private final AuthService authService;
    private final RoleService roleService;
    private final S3FileService  s3FileService;
    private final UserStatusHistoryRepository userStatusHistoryRepository;

    @Autowired
    public AdminServiceImpl(
            AdminRepository adminRepository,
            UserService userService,
            AuthService authService,
            RoleService roleService,
            S3FileService s3FileService1,
            UserStatusHistoryRepository userStatusHistoryRepository) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.authService = authService;
        this.roleService = roleService;
        this.s3FileService = s3FileService1;
        this.userStatusHistoryRepository = userStatusHistoryRepository;
    }

    @Override
    public AdminResponse addAdmin(AdminRequest adminRequest, MultipartFile imageFile) throws Exception {
        if (adminRequest.getEmail() == null || adminRequest.getEmail().isEmpty() || adminRequest.getRoleIds() == null || adminRequest.getRoleIds().isEmpty()) {
            throw new FunctionalException("Missing data");
        }

        if (userService.checkIfUserExistsByEmail(adminRequest.getEmail())) {
            throw new FunctionalException(MessageConstants.USER_EXISTS_EMAIL);
        }
        Admin admin = AdminMapper.requestToEntity(adminRequest);
        List<String> keycloakRoles = new ArrayList<>();
        List<Long> roleIds = adminRequest.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Role> roles = roleService.getAllRolesByIds(roleIds);
            admin.setRoles(roles);

            List<String> rolesName = roles.stream()
                    .map(Role::getName)
                    .toList();
            keycloakRoles.addAll(rolesName);
        }

        String imageFileName = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            userService.uploadImage(imageFile);
            imageFileName = imageFile.getOriginalFilename();
        }
        admin.setPhoto(imageFileName);

        admin.setPassword(userService.generatePassword());

        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest(
                admin.getEmail(),
                admin.getPassword(),
                admin.getFirstName(),
                admin.getLastName(),
                "default-roles-brainboost-local"
        );
        authService.registerUser(clientRegistrationRequest);
        if (!keycloakRoles.isEmpty()) {
            for (String role : keycloakRoles) {
                AddUserRoleRequest addUserRoleRequest = new AddUserRoleRequest(admin.getEmail(), role);
                authService.addUserRole(addUserRoleRequest);
            }
        }
        Admin savedAdmin = adminRepository.save(admin);

        AdminResponse adminResponse = AdminMapper.entityToResponse(savedAdmin);

        if (savedAdmin.getRoles() != null) {
            adminResponse.setRoles(savedAdmin.getRoles().stream()
                    .map(RoleMapper::roleToRoleResponse)
                    .collect(Collectors.toList()));
        }

        return adminResponse;

    }

    @Transactional
    @Override
    public AdminResponse updateAdmin(Long id, AdminRequest adminRequest, MultipartFile imageFile) throws Exception {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(MessageConstants.USER_NOT_FOUND + id));

        if (!existingAdmin.getEmail().equals(adminRequest.getEmail()) &&
                userService.checkIfUserExistsByEmail(adminRequest.getEmail())) {
            throw new FunctionalException(MessageConstants.USER_EXISTS_EMAIL);
        }

        String oldEmail = existingAdmin.getEmail();

        existingAdmin.setLastName(adminRequest.getLastName());
        existingAdmin.setFirstName(adminRequest.getFirstName());
        existingAdmin.setEmail(adminRequest.getEmail());
        existingAdmin.setBirthDate(adminRequest.getBirthDate());
        existingAdmin.setPhoneNumber(adminRequest.getPhoneNumber());
        existingAdmin.setPrimaryAddress(
                AddressMapper.addressRequestToAddress(adminRequest.getPrimaryAddress())
        );
        existingAdmin.setSecondaryAddress(
                AddressMapper.addressRequestToAddress(adminRequest.getSecondaryAddress())
        );

        String imageFileName = existingAdmin.getPhoto();
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFileName != null && !imageFileName.isEmpty()) {
                s3FileService.deleteSingleFile(imageFileName);
            }
            imageFileName = imageFile.getOriginalFilename();
            userService.uploadImage(imageFile);
        }
        existingAdmin.setPhoto(imageFileName);

        authService.updateUserWithEmail(new UpdateUserWithEmailRequest(
                oldEmail,
                existingAdmin.getFirstName(),
                existingAdmin.getLastName(),
                existingAdmin.getEmail()
        ));

        Admin savedAdmin = adminRepository.save(existingAdmin);

        AdminResponse adminResponse = AdminMapper.entityToResponse(savedAdmin);
        adminResponse.setRoles(savedAdmin.getRoles().stream()
                .map(RoleMapper::roleToRoleResponse)
                .collect(Collectors.toList()));

        return adminResponse;
    }

    public AdminResponse updateAdminRoles(Long id, List<Long> roleIds) throws Exception {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(MessageConstants.USER_NOT_FOUND + id));

        if (CollectionUtils.isEmpty(roleIds)) {
            throw new FunctionalException(MessageConstants.ADMIN_ROLE_LIST_NOT_NULL);
        }

        String adminEmail = existingAdmin.getEmail();
        List<Role> existingRoles = existingAdmin.getRoles();
        List<Role> newRoles = roleService.getAllRolesByIds(roleIds);

        if (existingRoles != null) {
            for (Role oldRole : existingRoles) {
                authService.removeUserRole(new RemoveUserRoleRequest(
                        adminEmail, oldRole.getName()));
            }
        }

        if (newRoles != null) {
            for (Role newRole : newRoles) {
                authService.addUserRole(new AddUserRoleRequest(
                        adminEmail, newRole.getName()));
            }
        }

        existingAdmin.setRoles(newRoles);
        Admin savedAdmin = adminRepository.save(existingAdmin);

        AdminResponse adminResponse = AdminMapper.entityToResponse(savedAdmin);
        adminResponse.setRoles(savedAdmin.getRoles().stream()
                .map(RoleMapper::roleToRoleResponse)
                .collect(Collectors.toList()));

        return adminResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public AdminResponse getAdminById(Long id) throws FunctionalException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));

        AdminResponse response = AdminMapper.entityToResponse(admin);
        return response;
    }

    @Override
    public AdminResponse softDeleteAdmin(Long id) throws TechnicalException, FunctionalException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (admin.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.USER_SOFT_DELETE_ERROR + admin.getStatus());
        }
        admin.getRoles().clear();
        admin.setStatus(UserStatus.DELETED);
        adminRepository.save(admin);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(admin);
        history.setStatus(UserStatus.DELETED);

        userStatusHistoryRepository.save(history);

        return AdminMapper.entityToResponse(admin);
    }
    @Override
    public void blockAdmin(Long id, String reason) throws FunctionalException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (admin.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.BLOCK_DELETED_USER_ERROR + admin.getStatus());
        }

        admin.setStatus(UserStatus.BLOCKED);
        adminRepository.save(admin);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(admin);
        history.setStatus(UserStatus.BLOCKED);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void reactivateAdmin(Long id) throws FunctionalException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND + id));

        if (admin.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.REACTIVATE_DELETED_USER_ERROR + admin.getStatus());
        }

        admin.setStatus(UserStatus.ACTIVE);
        adminRepository.save(admin);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(admin);
        history.setStatus(UserStatus.ACTIVE);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void suspendAdmin(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (admin.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.SUSPEND_DELETED_USER_ERROR + admin.getStatus());
        }

        admin.setStatus(UserStatus.SUSPENDED);
        adminRepository.save(admin);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(admin);
        history.setStatus(UserStatus.SUSPENDED);
        history.setStatusEnd(suspensionEnd);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public AdminResponse currentAdmin(JwtAuthenticationToken jwtToken) throws FunctionalException {
            if (jwtToken == null) {
                throw  new FunctionalException( "Token JWT manquant");
            }
        var jwt = jwtToken.getToken();

        String email = jwt.getClaimAsString("email");
        Admin admin = adminRepository.findByEmail(email);
        if (admin == null) {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }
        return AdminMapper.entityToResponse(admin);
    }

    @Override
    public AdminResponse updateCurrentAdmin(JwtAuthenticationToken jwtToken, AdminRequest adminRequest) throws FunctionalException {
        if (jwtToken == null) {
            throw  new FunctionalException( "Token JWT manquant");
        }
        var jwt = jwtToken.getToken();

        String email = jwt.getClaimAsString("email");
        Admin existingAdmin = adminRepository.findByEmail(email);
        if (existingAdmin == null) {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }
        if (!existingAdmin.getEmail().equals(adminRequest.getEmail()) &&
                userService.checkIfUserExistsByEmail(adminRequest.getEmail())) {
            throw new FunctionalException(MessageConstants.USER_EXISTS_EMAIL);
        }

        String oldEmail = existingAdmin.getEmail();

        existingAdmin.setLastName(adminRequest.getLastName());
        existingAdmin.setFirstName(adminRequest.getFirstName());
        existingAdmin.setEmail(adminRequest.getEmail());
        if (adminRequest.getBirthDate() != null) {
            existingAdmin.setBirthDate(adminRequest.getBirthDate());
        }
        existingAdmin.setPhoneNumber(adminRequest.getPhoneNumber());

        if (adminRequest.getPrimaryAddress() != null) {
            existingAdmin.setPrimaryAddress(
                    AddressMapper.addressRequestToAddress(adminRequest.getPrimaryAddress())
            );
        }

        if (adminRequest.getSecondaryAddress() != null) {
            existingAdmin.setSecondaryAddress(
                    AddressMapper.addressRequestToAddress(adminRequest.getSecondaryAddress())
            );
        }

        authService.updateUserWithEmail(new UpdateUserWithEmailRequest(
                oldEmail,
                existingAdmin.getFirstName(),
                existingAdmin.getLastName(),
                existingAdmin.getEmail()
        ));

        Admin savedAdmin = adminRepository.save(existingAdmin);

        AdminResponse adminResponse = AdminMapper.entityToResponse(savedAdmin);
        adminResponse.setRoles(savedAdmin.getRoles().stream()
                .map(RoleMapper::roleToRoleResponse)
                .collect(Collectors.toList()));

        return adminResponse;
    }
}
