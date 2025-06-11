package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.AdminRequest;
import com.marketingconfort.brainboost.dto.request.ChildRequest;
import com.marketingconfort.brainboost.dto.response.AdminResponse;
import com.marketingconfort.brainboost.dto.response.ChildResponse;
import com.marketingconfort.brainboost.dto.response.ParentSummaryResponse;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.Admin;
import com.marketingconfort.brainboost_common.usermanagement.models.Child;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

public class ChildMapper {
    public static Child requestToEntity(ChildRequest childRequest) {
        if (childRequest == null) {
            return null;
        }
        Child child = new Child();
        child.setLastName(childRequest.getLastName());
        child.setFirstName(childRequest.getFirstName());
        child.setEmail(childRequest.getEmail());
        child.setBirthDate(childRequest.getBirthDate());
        child.setStatus(UserStatus.ACTIVE);
        child.setPhoneNumber(childRequest.getPhoneNumber());
        child.setPhoto(childRequest.getPhoto());
        if (childRequest.getPrimaryAddress() != null) {
            child.setPrimaryAddress(
                    AddressMapper.addressRequestToAddress(childRequest.getPrimaryAddress())
            );
        }

        if (childRequest.getSecondaryAddress() != null) {
            child.setSecondaryAddress(
                    AddressMapper.addressRequestToAddress(childRequest.getSecondaryAddress())
            );
        }

        return child;
    }

    public static ChildResponse entityToResponse(Child child) {
        if (child == null) {
            return null;
        }
        ChildResponse childResponse = new ChildResponse();
        childResponse.setId(child.getId());
        childResponse.setLastName(child.getLastName());
        childResponse.setFirstName(child.getFirstName());
        childResponse.setEmail(child.getEmail());
        childResponse.setBirthDate(child.getBirthDate());
        childResponse.setStatus(child.getStatus());
        childResponse.setPhoneNumber(child.getPhoneNumber());
        childResponse.setPhoto(child.getPhoto());
        childResponse.setLastLogin(child.getLastLogin());
        childResponse.setCreatedAt(child.getCreatedAt());
        childResponse.setUserType(UserType.CHILD);
        if (child.getPrimaryAddress() != null) {
            childResponse.setPrimaryAddress(
                    AddressMapper.addressToAddressResponse(child.getPrimaryAddress())
            );
        }

        if (child.getSecondaryAddress() != null) {
            childResponse.setSecondaryAddress(
                    AddressMapper.addressToAddressResponse(child.getSecondaryAddress())
            );
        }
        childResponse.setTotalTimeSpent(child.getTotalTimeSpent());
        if (child.getParent() != null) {
            ParentSummaryResponse parentSummaryresponse = ParentMapper.entityToSummaryResponse(child.getParent());
            childResponse.setParent(parentSummaryresponse);
        }
        if (!CollectionUtils.isEmpty(child.getTimeScreens())) {
            childResponse.setTimeScreens(child.getTimeScreens().stream()
                    .map(TimeScreenMapper::timeScreenToTimeScreenResponse)
                    .collect(Collectors.toList()));
        }

        return childResponse;
    }
}
