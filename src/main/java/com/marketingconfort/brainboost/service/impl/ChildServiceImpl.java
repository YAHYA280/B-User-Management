package com.marketingconfort.brainboost.service.impl;

import com.marketingconfort.brainboost.constants.MessageConstants;
import com.marketingconfort.brainboost.dto.request.ChildRequest;
import com.marketingconfort.brainboost.dto.response.*;
import com.marketingconfort.brainboost.mapper.AddressMapper;
import com.marketingconfort.brainboost.mapper.AdminMapper;
import com.marketingconfort.brainboost.mapper.ChildMapper;
import com.marketingconfort.brainboost.mapper.UserMapper;
import com.marketingconfort.brainboost.repository.ChildRepository;
import com.marketingconfort.brainboost.repository.ParentRepository;
import com.marketingconfort.brainboost.repository.UserStatusHistoryRepository;
import com.marketingconfort.brainboost.service.ChildService;
import com.marketingconfort.brainboost.service.UserService;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.Child;
import com.marketingconfort.brainboost_common.usermanagement.models.Parent;
import com.marketingconfort.brainboost_common.usermanagement.models.UserStatusHistory;
import com.marketingconfort.starter.core.dtos.requests.ClientRegistrationRequest;
import com.marketingconfort.starter.core.dtos.requests.UpdateUserWithEmailRequest;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import com.marketingconfort.starter.core.services.AuthService;
import com.marketingconfort.starter.core.services.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChildServiceImpl implements ChildService {
    private final ChildRepository childRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ParentRepository parentRepository;
    private final S3FileService s3FileService;
    private final UserStatusHistoryRepository userStatusHistoryRepository;

    @Autowired
    public ChildServiceImpl(
            ChildRepository childRepository,
            UserService userService,
            AuthService authService,
            ParentRepository parentRepository, S3FileService s3FileService, UserStatusHistoryRepository userStatusHistoryRepository) {
        this.childRepository = childRepository;
        this.userService = userService;
        this.authService = authService;
        this.parentRepository = parentRepository;
        this.s3FileService = s3FileService;
        this.userStatusHistoryRepository = userStatusHistoryRepository;
    }

    @Override
    public ChildResponse addChild(ChildRequest childRequest, MultipartFile imageFile) throws Exception {
        if (childRequest.getEmail() == null || childRequest.getEmail().isEmpty()) {
            throw new FunctionalException("Missing data");
        }

        if (userService.checkIfUserExistsByEmail(childRequest.getEmail())) {
            throw new FunctionalException("Email already exists");
        }

        Child child = ChildMapper.requestToEntity(childRequest);
        if (childRequest.getParentId() != null) {
            Parent parentUser = parentRepository.findById(childRequest.getParentId())
                    .orElseThrow(() -> new FunctionalException(MessageConstants.PARENT_NOT_FOUND));
            if (parentUser.getStatus() == UserStatus.DELETED){
                throw new FunctionalException(MessageConstants.PARENT_DELETED);
            }
            child.setParent(parentUser);
        }
        String imageFileName = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            userService.uploadImage(imageFile);
            imageFileName = imageFile.getOriginalFilename();
        }
        child.setPhoto(imageFileName);

        child.setPassword(userService.generatePassword());

        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest(
                child.getEmail(),
                child.getPassword(),
                child.getFirstName(),
                child.getLastName(),
                UserType.CHILD.name().toLowerCase()
        );
        authService.registerUser(clientRegistrationRequest);
        Child savedChild = childRepository.save(child);

        return ChildMapper.entityToResponse(savedChild);
    }

    @Transactional
    @Override
    public ChildResponse updateChild(Long id, ChildRequest childRequest, MultipartFile imageFile) throws Exception {
        Child existingChild = childRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(MessageConstants.USER_NOT_FOUND));

        if (!existingChild.getEmail().equals(childRequest.getEmail()) &&
                userService.checkIfUserExistsByEmail(childRequest.getEmail())) {
            throw new FunctionalException(MessageConstants.USER_EXISTS_EMAIL);
        }
        if (childRequest.getParentId() != null) {
            Parent parentUser = parentRepository.findById(childRequest.getParentId())
                    .orElseThrow(() -> new FunctionalException(MessageConstants.PARENT_NOT_FOUND));
            if (parentUser.getStatus() == UserStatus.DELETED){
                throw new FunctionalException(MessageConstants.PARENT_DELETED);
            }
            existingChild.setParent(parentUser);
        }

        String oldEmail = existingChild.getEmail();

        existingChild.setLastName(childRequest.getLastName());
        existingChild.setFirstName(childRequest.getFirstName());
        existingChild.setEmail(childRequest.getEmail());
        existingChild.setBirthDate(childRequest.getBirthDate());
        existingChild.setPhoneNumber(childRequest.getPhoneNumber());
        existingChild.setPrimaryAddress(
                AddressMapper.addressRequestToAddress(childRequest.getPrimaryAddress())
        );
        existingChild.setSecondaryAddress(
                AddressMapper.addressRequestToAddress(childRequest.getSecondaryAddress())
        );

        String imageFileName = existingChild.getPhoto();
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFileName != null && !imageFileName.isEmpty()) {
                s3FileService.deleteSingleFile(imageFileName);
            }
            imageFileName = imageFile.getOriginalFilename();
            userService.uploadImage(imageFile);
        }
        existingChild.setPhoto(imageFileName);

        authService.updateUserWithEmail(new UpdateUserWithEmailRequest(
                oldEmail,
                existingChild.getFirstName(),
                existingChild.getLastName(),
                existingChild.getEmail()
        ));

        Child savedChild = childRepository.save(existingChild);

        ChildResponse childResponse = ChildMapper.entityToResponse(savedChild);

        return childResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public ChildResponse getChildById(Long id) throws FunctionalException {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        ChildResponse response = ChildMapper.entityToResponse(child);
        return response;
    }

    @Override
    public ChildResponse softDeleteChild(Long id) throws FunctionalException {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (child.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.USER_SOFT_DELETE_ERROR + child.getStatus());
        }
        child.setStatus(UserStatus.DELETED);
        childRepository.save(child);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(child);
        history.setStatus(UserStatus.DELETED);

        userStatusHistoryRepository.save(history);

        return ChildMapper.entityToResponse(child);
    }

    @Override
    public void blockChild(Long id, String reason) throws FunctionalException {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (child.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.BLOCK_DELETED_USER_ERROR + child.getStatus());
        }

        child.setStatus(UserStatus.BLOCKED);
        childRepository.save(child);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(child);
        history.setStatus(UserStatus.BLOCKED);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void reactivateChild(Long id) throws FunctionalException {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND + id));

        if (child.getStatus() == UserStatus.DELETED) {
            throw new FunctionalException(MessageConstants.REACTIVATE_DELETED_USER_ERROR + child.getStatus());
        }

        child.setStatus(UserStatus.ACTIVE);
        childRepository.save(child);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(child);
        history.setStatus(UserStatus.ACTIVE);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public void suspendChild(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        if (child.getStatus() == UserStatus.SUSPENDED) {
            throw new FunctionalException(MessageConstants.SUSPEND_DELETED_USER_ERROR + child.getStatus());
        }

        child.setStatus(UserStatus.SUSPENDED);
        childRepository.save(child);

        UserStatusHistory history = new UserStatusHistory();
        history.setUser(child);
        history.setStatus(UserStatus.SUSPENDED);
        history.setStatusEnd(suspensionEnd);
        history.setReason(reason);

        userStatusHistoryRepository.save(history);
    }

    @Override
    public List<UserResponse> suspendChildrenByParentId(Long id, LocalDate suspensionEnd) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        List<UserResponse> modifiedChildren = new ArrayList<>();
        if (parent.getChildren() != null) {
            for (Child child : parent.getChildren()) {
                if (child.getStatus() != UserStatus.DELETED){
                    child.setStatus(UserStatus.SUSPENDED);
                    childRepository.save(child);

                    UserStatusHistory history = new UserStatusHistory();
                    history.setUser(child);
                    history.setStatus(UserStatus.SUSPENDED);
                    history.setStatusEnd(suspensionEnd);
                    history.setReason("Parent Deleted");

                    userStatusHistoryRepository.save(history);
                    modifiedChildren.add(UserMapper.entityToResponse(child));
                }
            }
        }
        return modifiedChildren;
    }

    @Override
    public List<UserResponse> blockChildrenByParentId(Long id) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        List<UserResponse> modifiedChildren = new ArrayList<>();
        if (parent.getChildren() != null) {
            for (Child child : parent.getChildren()) {
                if (child.getStatus() != UserStatus.DELETED){
                    child.setStatus(UserStatus.BLOCKED);
                    childRepository.save(child);
                    UserStatusHistory history = new UserStatusHistory();
                    history.setUser(child);
                    history.setStatus(UserStatus.BLOCKED);
                    history.setReason("Parent Blocked");

                    userStatusHistoryRepository.save(history);
                    modifiedChildren.add(UserMapper.entityToResponse(child));
                }
            }
        }
        return modifiedChildren;
    }

    @Override
    public List<UserResponse> softDeleteChildrenByParentId(Long id) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        List<UserResponse> modifiedChildren = new ArrayList<>();
        if (parent.getChildren() != null) {
            for (Child child : parent.getChildren()) {
                if (child.getStatus() != UserStatus.DELETED){
                    child.setStatus(UserStatus.DELETED);
                    childRepository.save(child);
                    UserStatusHistory history = new UserStatusHistory();
                    history.setUser(child);
                    history.setStatus(UserStatus.DELETED);

                    userStatusHistoryRepository.save(history);
                    modifiedChildren.add(UserMapper.entityToResponse(child));
                }
            }
        }
        return modifiedChildren;
    }

    @Override
    public List<UserResponse> reactivateChildrenByParentId(Long id) throws FunctionalException {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException(MessageConstants.USER_NOT_FOUND+ id));
        List<UserResponse> modifiedChildren = new ArrayList<>();
        if (parent.getChildren() != null) {
            for (Child child : parent.getChildren()) {
                if (child.getStatus() != UserStatus.DELETED){
                    child.setStatus(UserStatus.ACTIVE);
                    childRepository.save(child);
                    UserStatusHistory history = new UserStatusHistory();
                    history.setUser(child);
                    history.setStatus(UserStatus.ACTIVE);

                    userStatusHistoryRepository.save(history);
                    modifiedChildren.add(UserMapper.entityToResponse(child));
                }
            }
        }
        return modifiedChildren;
    }
}
