package com.marketingconfort.brainboost.service;

import com.marketingconfort.brainboost.dto.request.ParentRequest;
import com.marketingconfort.brainboost.dto.response.ParentResponse;
import com.marketingconfort.brainboost.dto.response.ParentStripeResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ParentService {
    ParentResponse addParent(ParentRequest parentRequest, MultipartFile imageFile) throws Exception;
    @Transactional
    ParentResponse updateParent(Long id, ParentRequest parentRequest, MultipartFile imageFile) throws Exception;
    @Transactional(readOnly = true)
    ParentResponse getParentById(Long id) throws FunctionalException;
    ParentStripeResponse getParentByCustomerId(String customerId) throws FunctionalException;
    ParentResponse softDeleteParent(Long id) throws TechnicalException, FunctionalException;
    void blockParent(Long id, String reason) throws FunctionalException;
    void reactivateParent(Long id) throws FunctionalException;
    void suspendParent(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException;
    void updateGatewayCustomerId(Long parentId, String gatewayCustomerId) throws FunctionalException;
    List<Long> getParentIdsByNameLike(String name);
    ParentResponse currentParent(JwtAuthenticationToken jwtToken) throws FunctionalException;
}
