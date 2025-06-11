package com.marketingconfort.brainboost.service.impl;

import com.marketingconfort.brainboost.constants.MessageConstants;
import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.brainboost.dto.response.UsersResponse;
import com.marketingconfort.brainboost.mapper.UserMapper;
import com.marketingconfort.brainboost.repository.AdminRepository;
import com.marketingconfort.brainboost.repository.ChildRepository;
import com.marketingconfort.brainboost.repository.ParentRepository;
import com.marketingconfort.brainboost.repository.UserRepository;
import com.marketingconfort.brainboost.service.RoleService;
import com.marketingconfort.brainboost.service.UserService;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.*;
import com.marketingconfort.starter.core.dtos.requests.AuthenticationRequest;
import com.marketingconfort.starter.core.dtos.responses.AuthenticationResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.S3FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import com.marketingconfort.starter.core.services.AuthService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.marketingconfort.starter.core.services.S3FileService;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final S3FileService  s3FileService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final AuthService authService;


    @Autowired
    public UserServiceImpl(
            S3FileService  s3FileService, UserRepository userRepository, AdminRepository adminRepository, ParentRepository parentRepository, ChildRepository childRepository, AuthService authService) {
        this.s3FileService = s3FileService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.parentRepository = parentRepository;
        this.childRepository = childRepository;
        this.authService = authService;
    }

    @Override
    public String generatePassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?/";

        String allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        for (int i = 4; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        for (int i = 0; i < password.length(); i++) {
            int j = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }

    @Override
    public boolean checkIfUserExistsByEmail(String email) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null;
    }

    @Override
    public String uploadImage(MultipartFile file) throws S3FunctionalException {
        return s3FileService.uploadSingleFile(file);

    }
    @Transactional(readOnly = true)
    @Override
    public UsersResponse getAllUsers(int page, int size, String sortBy, String sortDirection,
                                     String nameSearch, String emailSearch, String statusSearch,
                                     String dateSearch, String lastLoginSearch, String roleSearch) {

        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Comparator<UserResponse> comparator = Comparator.comparing(
                (UserResponse user) -> {
                    switch (sortBy) {
                        case "firstName": return (Comparable) user.getFirstName();
                        case "lastName": return (Comparable) user.getLastName();
                        case "email": return (Comparable) user.getEmail();
                        case "status": return (Comparable) user.getStatus();
                        case "createdAt": return (Comparable) user.getCreatedAt();
                        case "lastLogin": return (Comparable) user.getLastLogin();
                        default: return (Comparable) user.getCreatedAt();
                    }
                },
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        if (direction == Sort.Direction.DESC) comparator = comparator.reversed();

        List<UserResponse> totalUsers = new ArrayList<>();
        List<UserResponse> filteredUsers = new ArrayList<>();
        Map<String, Long> statusCounts = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Admin> admins = adminRepository.findAll();
        List<Parent> parents = parentRepository.findAll();
        List<Child> children = childRepository.findAll();

        for (Admin admin : admins) {
            UserResponse dto = UserMapper.entityToResponse(admin);
            dto.setUserType(UserType.ADMIN);
            totalUsers.add(dto);
        }
        for (Parent parent : parents) {
            UserResponse dto = UserMapper.entityToResponse(parent);
            dto.setUserType(UserType.PARENT);
            totalUsers.add(dto);
        }
        for (Child child : children) {
            UserResponse dto = UserMapper.entityToResponse(child);
            dto.setUserType(UserType.CHILD);
            totalUsers.add(dto);
        }

        for (UserResponse user : totalUsers) {
            String status = user.getStatus().name();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
        }
        statusCounts.put("Tous", (long) totalUsers.size());

        for (UserResponse user : totalUsers) {
            boolean matchRole = roleSearch == null || roleSearch.isEmpty() || user.getUserType().name().equalsIgnoreCase(roleSearch);
            if (matchRole && matchFilters(user, nameSearch, emailSearch, statusSearch, dateSearch, lastLoginSearch, formatter)) {
                filteredUsers.add(user);
            }
        }

        filteredUsers.sort(comparator);
        int start = page * size;
        int end = Math.min(start + size, filteredUsers.size());
        List<UserResponse> pageContent = start < filteredUsers.size() ? filteredUsers.subList(start, end) : List.of();

        return new UsersResponse(pageContent, totalUsers.size(), statusCounts);
    }

    @Override
    public AuthenticationResponse authenticateBo(AuthenticationRequest authenticationRequest) throws TechnicalException, FunctionalException {
        User existingUser = userRepository.findByEmail(authenticationRequest.username());
        if (existingUser != null) {
            Optional<Admin> adminOpt = adminRepository.findById(existingUser.getId());
            if (!adminOpt.isPresent()) {
                throw new FunctionalException(MessageConstants.ACCESS_DENIED);
            }

            switch (existingUser.getStatus()) {
                case ACTIVE -> {
                    AuthenticationResponse authenticationResponse = authService.getToken(new AuthenticationRequest(
                            existingUser.getEmail(),
                            authenticationRequest.password()
                    ));

                    return authenticationResponse;
                }
                case DELETED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_DELETED);
                }
                case SUSPENDED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_SUSPENDED);
                }
                case BLOCKED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_BLOCKED);
                }
                default -> throw new FunctionalException(MessageConstants.USER_STATUS_NOT_RECOGNIZED);
            }
        } else {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }
    }

    @Override
    public AuthenticationResponse authenticateAp(AuthenticationRequest authenticationRequest) throws TechnicalException, FunctionalException {
        User existingUser = userRepository.findByEmail(authenticationRequest.username());
        if (existingUser != null) {
            Optional<Parent> parentOpt = parentRepository.findById(existingUser.getId());
            if (!parentOpt.isPresent()) {
                throw new FunctionalException(MessageConstants.ACCESS_DENIED_AP);
            }

            switch (existingUser.getStatus()) {
                case ACTIVE -> {
                    AuthenticationResponse authenticationResponse = authService.getToken(new AuthenticationRequest(
                            existingUser.getEmail(),
                            authenticationRequest.password()
                    ));

                    return authenticationResponse;
                }
                case DELETED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_DELETED);
                }
                case SUSPENDED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_SUSPENDED);
                }
                case BLOCKED -> {
                    throw new FunctionalException(MessageConstants.ACCOUNT_BLOCKED);
                }
                default -> throw new FunctionalException(MessageConstants.USER_STATUS_NOT_RECOGNIZED);
            }
        } else {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }
    }

    private boolean matchFilters(UserResponse user, String nameSearch, String emailSearch,
                                 String statusSearch, String dateSearch,
                                 String lastLoginSearch, DateTimeFormatter formatter) {
        if (nameSearch != null && !nameSearch.isEmpty()) {
            String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
            if (!fullName.contains(nameSearch.toLowerCase())) return false;
        }

        if (emailSearch != null && !emailSearch.isEmpty()) {
            if (user.getEmail() == null || !user.getEmail().toLowerCase().contains(emailSearch.toLowerCase())) return false;
        }

        if (statusSearch != null && !statusSearch.isEmpty()) {
            try {
                if (!user.getStatus().name().equalsIgnoreCase(statusSearch)) return false;
            } catch (IllegalArgumentException ignored) {}
        }

        if (dateSearch != null && !dateSearch.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateSearch, formatter);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay();
                if (user.getCreatedAt() == null || user.getCreatedAt().isBefore(start) || user.getCreatedAt().isAfter(end)) return false;
            } catch (Exception ignored) {}
        }

        if (lastLoginSearch != null && !lastLoginSearch.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(lastLoginSearch, formatter);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay();
                if (user.getLastLogin() == null || user.getLastLogin().isBefore(start) || user.getLastLogin().isAfter(end)) return false;
            } catch (Exception ignored) {}
        }

        return true;
    }

}
