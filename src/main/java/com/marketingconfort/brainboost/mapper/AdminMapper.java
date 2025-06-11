package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.AdminRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.Admin;

import java.util.stream.Collectors;

public class AdminMapper {
    public static Admin requestToEntity(AdminRequest adminRequest) {
        if (adminRequest == null) {
            return null;
        }
        Admin admin = new Admin();
        admin.setLastName(adminRequest.getLastName());
        admin.setFirstName(adminRequest.getFirstName());
        admin.setEmail(adminRequest.getEmail());
        admin.setBirthDate(adminRequest.getBirthDate());
        admin.setStatus(UserStatus.ACTIVE);
        admin.setPhoneNumber(adminRequest.getPhoneNumber());
        admin.setPhoto(adminRequest.getPhoto());
        if (adminRequest.getPrimaryAddress() != null) {
            admin.setPrimaryAddress(
                    AddressMapper.addressRequestToAddress(adminRequest.getPrimaryAddress())
            );
        }

        if (adminRequest.getSecondaryAddress() != null) {
            admin.setSecondaryAddress(
                    AddressMapper.addressRequestToAddress(adminRequest.getSecondaryAddress())
            );
        }

        return admin;
    }

    public static AdminResponse entityToResponse(Admin admin) {
        if (admin == null) {
            return null;
        }
        AdminResponse adminResponse = new AdminResponse();
        adminResponse.setId(admin.getId());
        adminResponse.setLastName(admin.getLastName());
        adminResponse.setFirstName(admin.getFirstName());
        adminResponse.setEmail(admin.getEmail());
        adminResponse.setBirthDate(admin.getBirthDate());
        adminResponse.setStatus(admin.getStatus());
        adminResponse.setPhoneNumber(admin.getPhoneNumber());
        adminResponse.setPhoto(admin.getPhoto());
        adminResponse.setLastLogin(admin.getLastLogin());
        adminResponse.setCreatedAt(admin.getCreatedAt());
        adminResponse.setUserType(UserType.ADMIN);
        adminResponse.setRoles(
                admin.getRoles().stream()
                        .map(RoleMapper::roleToRoleResponse)
                        .collect(Collectors.toList())
        );
        if (admin.getPrimaryAddress() != null) {
            adminResponse.setPrimaryAddress(
                    AddressMapper.addressToAddressResponse(admin.getPrimaryAddress())
            );
        }

        if (admin.getSecondaryAddress() != null) {
            adminResponse.setSecondaryAddress(
                    AddressMapper.addressToAddressResponse(admin.getSecondaryAddress())
            );
        }

        return adminResponse;
    }
}
