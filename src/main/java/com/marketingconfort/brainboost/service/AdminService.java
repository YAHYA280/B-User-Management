package com.marketingconfort.brainboost.service;

import com.marketingconfort.brainboost.dto.request.AdminRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.starter.core.exceptions.FunctionalException;
import com.marketingconfort.starter.core.exceptions.TechnicalException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    AdminResponse addAdmin(AdminRequest adminRequest, MultipartFile imageFile) throws Exception;
    @Transactional
    AdminResponse updateAdmin(Long id, AdminRequest adminRequest, MultipartFile imageFile) throws Exception;
    AdminResponse updateAdminRoles(Long id, List<Long> roleIds) throws Exception;
    @Transactional(readOnly = true)
    AdminResponse getAdminById(Long id) throws FunctionalException;
    AdminResponse softDeleteAdmin(Long id) throws TechnicalException, FunctionalException;
    void blockAdmin(Long id, String reason) throws FunctionalException;
    void reactivateAdmin(Long id) throws FunctionalException;
    void suspendAdmin(Long id, String reason, LocalDate suspensionEnd) throws FunctionalException;
    AdminResponse currentAdmin(JwtAuthenticationToken jwtToken) throws FunctionalException;
    AdminResponse updateCurrentAdmin(JwtAuthenticationToken jwtToken,AdminRequest adminRequest ) throws FunctionalException;
}
