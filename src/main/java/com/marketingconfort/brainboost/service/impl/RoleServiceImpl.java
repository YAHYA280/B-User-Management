package com.marketingconfort.brainboost.service.impl;
import com.marketingconfort.brainboost.constants.MessageConstants;
import com.marketingconfort.brainboost.dto.request.PermissionRequest;
import com.marketingconfort.brainboost.dto.request.RoleRequest;
import com.marketingconfort.brainboost.dto.response.RoleResponse;
import com.marketingconfort.brainboost.mapper.PermissionMapper;
import com.marketingconfort.brainboost.mapper.RoleMapper;
import com.marketingconfort.brainboost.repository.AdminRepository;
import com.marketingconfort.brainboost.repository.RoleRepository;
import com.marketingconfort.brainboost.service.RoleService;
import com.marketingconfort.brainboost_common.usermanagement.models.Permission;
import com.marketingconfort.brainboost_common.usermanagement.models.Role;
import com.marketingconfort.starter.core.dtos.requests.RealmRoleCreationRequest;
import com.marketingconfort.starter.core.dtos.requests.RealmRoleUpdateRequest;
import com.marketingconfort.starter.core.dtos.responses.RealRoleResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.services.AuthService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final AdminRepository adminRepository;

    @Autowired
    public RoleServiceImpl(
            RoleRepository roleRepository,
            AuthService authService,
            AdminRepository adminRepository
            ) {
        this.roleRepository = roleRepository;
        this.authService = authService;
        this.adminRepository = adminRepository;
    }
    @Transactional
    @Override
    public RoleResponse addRole(RoleRequest request) throws FunctionalException {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new FunctionalException("Role already exists with the same name");
        }

        Role role = RoleMapper.roleRequestToRole(request);

        List<Permission> permissions = new ArrayList<>();

        for (PermissionRequest permReq : request.getPermissions()) {
            Permission permission = PermissionMapper.PermissionRequestToPermission(permReq);
            permission.setRole(role);

            permissions.add(permission);
        }

        role.setPermissions(permissions);

        RealmRoleCreationRequest realmRoleCreationRequest = new RealmRoleCreationRequest(
                role.getName(),
                role.getDescription());
        authService.createRoleInKeycloakRealm(realmRoleCreationRequest);
        roleRepository.save(role);

        return RoleMapper.roleToRoleResponse(role);
    }
    @Transactional
    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) throws FunctionalException {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.ROLE_NOT_FOUND));

        Optional<Role> roleWithSameName = roleRepository.findByName(request.getName());
        if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().equals(id)) {
            throw new FunctionalException(MessageConstants.ROLE_NAME_ALREADY_EXISTS);
        }
        String oldRole = existingRole.getName();
        existingRole.setName(request.getName());
        existingRole.setDescription(request.getDescription());

        List<Permission> updatedPermissions = new ArrayList<>();
        for (PermissionRequest permReq : request.getPermissions()) {
            Permission permission = PermissionMapper.PermissionRequestToPermission(permReq);
            permission.setRole(existingRole);
            updatedPermissions.add(permission);
        }
        existingRole.getPermissions().clear();
        existingRole.getPermissions().addAll(updatedPermissions);

        RealmRoleUpdateRequest realmUpdateRequest = new RealmRoleUpdateRequest(
                oldRole,
                existingRole.getName(),
                existingRole.getDescription()
        );
        authService.updateRoleInKeycloakRealm(realmUpdateRequest);

        roleRepository.save(existingRole);

        return RoleMapper.roleToRoleResponse(existingRole);
    }

    @Override
    public RoleResponse getRoleById(Long id) throws FunctionalException{
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return RoleMapper.roleToRoleResponse(role.get());
        } else {
            throw new FunctionalException(MessageConstants.ROLE_NOT_FOUND);
        }
    }

    @Override
    public RoleResponse getRoleByName(String name) throws FunctionalException{
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new FunctionalException("Role not found with name: " + name));
        return RoleMapper.roleToRoleResponse(role);
    }

    @Override
    public List<Role> getAllRolesByIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return roleRepository.findAllById(roleIds);
    }

    @Override
    public Page<RoleResponse> getAllRoles(int page, int size, String sortBy, String sortDirection,
                                          String name, String description, String createdAt) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Set<String> realmRoleNames = authService.getRealmRolesList().stream()
                .map(RealRoleResponse::name)
                .collect(Collectors.toSet());

        Specification<Role> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate realmRolesPredicate = root.get("name").in(realmRoleNames);
            predicates.add(realmRolesPredicate);

            if (name != null && !name.isEmpty()) {
                String namePattern = "%" + name.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }

            if (description != null && !description.isEmpty()) {
                String descPattern = "%" + description.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("description")), descPattern));
            }

            if (createdAt != null && !createdAt.isEmpty()) {
                try {
                    LocalDate searchDate = LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDateTime startOfDay = searchDate.atStartOfDay();
                    LocalDateTime endOfDay = searchDate.plusDays(1).atStartOfDay();
                    predicates.add(cb.between(root.get("createdAt"), startOfDay, endOfDay));
                } catch (Exception e) {
                    // dateSearch ignorée si invalide
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return roleRepository.findAll(specification, pageRequest)
                .map(RoleMapper::roleToRoleResponse);
    }
    @Transactional
    @Override
    public void deleteRole(Long id) throws FunctionalException {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.ROLE_NOT_FOUND));
        boolean isAssigned = adminRepository.existsByRoles(existingRole);
        if (isAssigned) {
            throw new FunctionalException(MessageConstants.ROLE_DELETE_ERROR_ASSIGNED_TO_ADMINS);
        }
//        authService.removeRoleFromKeycloakRealm(
//                existingRole.getName()
//        );

        roleRepository.delete(existingRole);
    }

    @Override
    public boolean isRoleAssigned(Long id) throws FunctionalException{
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.ROLE_NOT_FOUND));
        return adminRepository.existsByRoles(existingRole);
    }
}
