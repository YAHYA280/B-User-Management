package com.marketingconfort.brainboost.service;

import com.marketingconfort.brainboost.dto.request.BaseUserRequest;
import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.brainboost.dto.response.UsersResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.User;
import com.marketingconfort.starter.core.dtos.requests.AuthenticationRequest;
import com.marketingconfort.starter.core.dtos.responses.AuthenticationResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.S3FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {
    String generatePassword();
    boolean checkIfUserExistsByEmail(String email);
    String uploadImage(MultipartFile file) throws S3FunctionalException;
    @Transactional(readOnly = true)
    UsersResponse getAllUsers(int page, int size, String sortBy, String sortDirection,
                              String nameSearch, String emailSearch,
                              String statusSearch, String dateSearch, String lastLoginSearch, String roleSearch);
    AuthenticationResponse authenticateBo(AuthenticationRequest authenticationRequest) throws TechnicalException, FunctionalException;
    AuthenticationResponse authenticateAp(AuthenticationRequest authenticationRequest) throws TechnicalException, FunctionalException;
}
