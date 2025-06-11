package com.marketingconfort.brainboost.service.impl;

import com.marketingconfort.brainboost.constants.MessageConstants;
import com.marketingconfort.brainboost.dto.request.ParentRequest;
import com.marketingconfort.brainboost.dto.response.ParentResponse;
import com.marketingconfort.brainboost.dto.response.ParentStripeResponse;
import com.marketingconfort.brainboost.dto.response.UserStatusHistoryResponse;
import com.marketingconfort.brainboost.mapper.AddressMapper;
import com.marketingconfort.brainboost.mapper.AdminMapper;
import com.marketingconfort.brainboost.mapper.ParentMapper;
import com.marketingconfort.brainboost.repository.ParentRepository;
import com.marketingconfort.brainboost.repository.UserStatusHistoryRepository;
import com.marketingconfort.brainboost.service.ChildService;
import com.marketingconfort.brainboost.service.ParentService;
import com.marketingconfort.brainboost.service.UserService;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.Admin;
import com.marketingconfort.brainboost_common.usermanagement.models.Parent;
import com.marketingconfort.brainboost_common.usermanagement.models.UserStatusHistory;
import com.marketingconfort.starter.core.dtos.requests.ClientRegistrationRequest;
import com.marketingconfort.starter.core.dtos.requests.UpdateUserWithEmailRequest;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import com.marketingconfort.starter.core.services.AuthService;
import com.marketingconfort.starter.core.services.S3FileService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParentServiceImpl implements ParentService {
    private final ParentRepository parentRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ChildService childService;
    private final S3FileService s3FileService;
    private final UserStatusHistoryRepository userStatusHistoryRepository;

    @Autowired
    public ParentServiceImpl(
            ParentRepository parentRepository,
            UserService userService,
            AuthService authService,
            ChildService childService, S3FileService s3FileService, UserStatusHistoryRepository userStatusHistoryRepository) {
        this.parentRepository = parentRepository;
        this.userService = userService;
        this.authService = authService;
        this.childService = childService;
        this.s3FileService = s3FileService;
        this.userStatusHistoryRepository = userStatusHistoryRepository;
    }

    @Override
    public ParentResponse addParent(ParentRequest parentRequest, MultipartFile imageFile) throws Exception {
        if (parentRequest.getEmail() == null || parentRequest.getEmail().isEmpty()) {
            throw new FunctionalException("Missing data");
        }

        if (userService.checkIfUserExistsByEmail(parentRequest.getEmail())) {
            throw new FunctionalException("Email already exists");
        }

        Parent parent = ParentMapper.requestToEntity(parentRequest);

        String imageFileName = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            userService.uploadImage(imageFile);
            imageFileName = imageFile.getOriginalFilename();
        }
        parent.setPhoto(imageFileName);

        parent.setPassword(userService.generatePassword());

        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest(
                parent.getEmail(),
                parent.getPassword(),
                parent.getFirstName(),
                parent.getLastName(),
                UserType.PARENT.name().toLowerCase()
        );
        authService.registerUser(clientRegistrationRequest);
        Parent savedParent = parentRepository.save(parent);

        return ParentMapper.entityToResponse(savedParent);
    }

    @Transactional
    @Override
    public ParentResponse updateParent(Long id, ParentRequest parentRequest, MultipartFile imageFile) throws Exception {
        Parent existingParent = parentRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(MessageConstants.USER_NOT_FOUND));

        if (!existingParent.getEmail().equals(parentRequest.getEmail()) &&
                userService.checkIfUserExistsByEmail(parentRequest.getEmail())) {
            throw new FunctionalException(MessageConstants.USER_EXISTS_EMAIL);
        }
        String oldEmail = existingParent.getEmail();

        existingParent.setLastName(parentRequest.getLastName());
        existingParent.setFirstName(parentRequest.getFirstName());
        existingParent.setEmail(parentRequest.getEmail());
        existingParent.setBirthDate(parentRequest.getBirthDate());
        existingParent.setPhoneNumber(parentRequest.getPhoneNumber());
        existingParent.setPrimaryAddress(
                AddressMapper.addressRequestToAddress(parentRequest.getPrimaryAddress())
        );
        existingParent.setSecondaryAddress(
                AddressMapper.addressRequestToAddress(parentRequest.getSecondaryAddress())
        );

        String imageFileName = existingParent.getPhoto();
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFileName != null && !imageFileName.isEmpty()) {
                s3FileService.deleteSingleFile(imageFileName);
            }
            imageFileName = imageFile.getOriginalFilename();
            userService.uploadImage(imageFile);
        }
        existingParent.setPhoto(imageFileName);

        authService.updateUserWithEmail(new UpdateUserWithEmailRequest(
                oldEmail,
                existingParent.getFirstName(),
                existingParent.getLastName(),
                existingParent.getEmail()
        ));

        Parent savedParent = parentRepository.save(existingParent);

        ParentResponse parentResponse = ParentMapper.entityToResponse(savedParent);

        return parentResponse;
    }

    @Override
    public List<Long> getParentIdsByNameLike(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<Parent> parents = parentRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name.trim());
            return parents.stream()
                    .map(Parent::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Error fetching parent IDs by name '{}': {}", name, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ParentResponse getParentById(Long id) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));

        ParentResponse response = ParentMapper.entityToResponse(parent);
        return response;
    }

    @Override
    public ParentStripeResponse getParentByCustomerId(String customerId) throws FunctionalException {
        Optional<Parent> parentOpt = parentRepository.findByGatewayCustomerId(customerId);

        if (parentOpt.isEmpty()) {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }

        Parent parent = parentOpt.get();

        ParentStripeResponse response = ParentMapper.entityToStripeResponse(parent);

        return response;
    }

    @Override
    public ParentResponse softDeleteParent(Long id) throws TechnicalException, FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (parent.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.USER_SOFT_DELETE_ERROR + parent.getStatus());
        }

        parent.setStatus(UserStatus.DELETED);
        parentRepository.save(parent);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(parent);
        history.setStatus(UserStatus.DELETED);

        userStatusHistoryRepository.save(history);

        return ParentMapper.entityToResponse(parent);
    }

    @Override
    public void blockParent(Long id, String reason) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (parent.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.BLOCK_DELETED_USER_ERROR + parent.getStatus() + parent.getId());
        }

        parent.setStatus(UserStatus.BLOCKED);
        parentRepository.save(parent);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(parent);
        history.setStatus(UserStatus.BLOCKED);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void reactivateParent(Long id) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND + id));

        if (parent.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.REACTIVATE_DELETED_USER_ERROR + parent.getStatus() + " ID: " + parent.getId());
        }

        parent.setStatus(UserStatus.ACTIVE);
        parentRepository.save(parent);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(parent);
        history.setStatus(UserStatus.ACTIVE);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void suspendParent(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (parent.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.SUSPEND_DELETED_USER_ERROR + parent.getStatus() + parent.getId());
        }

        parent.setStatus(UserStatus.SUSPENDED);
        parentRepository.save(parent);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(parent);
        history.setStatus(UserStatus.SUSPENDED);
        history.setStatusEnd(suspensionEnd);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void updateGatewayCustomerId(Long parentId, String gatewayCustomerId) throws FunctionalException {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND + parentId));

        parent.setGatewayCustomerId(gatewayCustomerId);
        parentRepository.save(parent);
    }

    @Override
    public ParentResponse currentParent(JwtAuthenticationToken jwtToken) throws FunctionalException {
        if (jwtToken == null) {
            throw  new FunctionalException( "Token JWT manquant");
        }
        var jwt = jwtToken.getToken();

        String email = jwt.getClaimAsString("email");
        Parent parent = parentRepository.findByEmail(email);
        if (parent == null) {
            throw new FunctionalException(MessageConstants.USER_NOT_FOUND);
        }
        return ParentMapper.entityToResponse(parent);
    }
}
