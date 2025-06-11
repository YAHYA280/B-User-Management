package com.marketingconfort.brainboost.service;

import com.marketingconfort.brainboost.dto.request.ChildRequest;
import com.marketingconfort.brainboost.dto.response.ChildResponse;
import com.marketingconfort.brainboost.dto.response.ChildSummaryResponse;
import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ChildService {
    ChildResponse addChild(ChildRequest childRequest, MultipartFile imageFile) throws Exception;
    @Transactional
    ChildResponse updateChild(Long id, ChildRequest childRequest, MultipartFile imageFile) throws Exception;
    @Transactional(readOnly = true)
    ChildResponse getChildById(Long id) throws FunctionalException;
    ChildResponse softDeleteChild(Long id) throws FunctionalException;
    void blockChild(Long id, String reason) throws FunctionalException;
    void reactivateChild(Long id) throws FunctionalException;
    void suspendChild(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException;
    List<UserResponse> suspendChildrenByParentId(Long id, LocalDate suspensionEnd) throws FunctionalException;
    List<UserResponse> blockChildrenByParentId(Long id) throws FunctionalException;
    List<UserResponse> softDeleteChildrenByParentId(Long id) throws FunctionalException;
    List<UserResponse> reactivateChildrenByParentId(Long id) throws FunctionalException;
}
