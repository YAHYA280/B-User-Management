package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.ParentRequest;
import com.marketingconfort.brainboost.dto.response.ChildSummaryResponse;
import com.marketingconfort.brainboost.dto.response.ParentResponse;
import com.marketingconfort.brainboost.dto.response.ParentStripeResponse;
import com.marketingconfort.brainboost.dto.response.ParentSummaryResponse;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import com.marketingconfort.brainboost_common.usermanagement.enums.UserType;
import com.marketingconfort.brainboost_common.usermanagement.models.Parent;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ParentMapper {
    public static Parent requestToEntity(ParentRequest parentRequest) {
        if (parentRequest == null) {
            return null;
        }
        Parent parent = new Parent();
        parent.setLastName(parentRequest.getLastName());
        parent.setFirstName(parentRequest.getFirstName());
        parent.setEmail(parentRequest.getEmail());
        parent.setBirthDate(parentRequest.getBirthDate());
        parent.setStatus(UserStatus.ACTIVE);
        parent.setPhoneNumber(parentRequest.getPhoneNumber());
        parent.setPhoto(parentRequest.getPhoto());
        if (parentRequest.getPrimaryAddress() != null) {
            parent.setPrimaryAddress(
                    AddressMapper.addressRequestToAddress(parentRequest.getPrimaryAddress())
            );
        }

        if (parentRequest.getSecondaryAddress() != null) {
            parent.setSecondaryAddress(
                    AddressMapper.addressRequestToAddress(parentRequest.getSecondaryAddress())
            );
        }

        return parent;
    }

    public static ParentResponse entityToResponse(Parent parent) {
        if (parent == null) {
            return null;
        }
        ParentResponse parentResponse = new ParentResponse();
        parentResponse.setId(parent.getId());
        parentResponse.setLastName(parent.getLastName());
        parentResponse.setFirstName(parent.getFirstName());
        parentResponse.setEmail(parent.getEmail());
        parentResponse.setBirthDate(parent.getBirthDate());
        parentResponse.setStatus(parent.getStatus());
        parentResponse.setPhoneNumber(parent.getPhoneNumber());
        parentResponse.setPhoto(parent.getPhoto());
        parentResponse.setLastLogin(parent.getLastLogin());
        parentResponse.setCreatedAt(parent.getCreatedAt());
        parentResponse.setUserType(UserType.PARENT);
        if (parent.getPrimaryAddress() != null) {
            parentResponse.setPrimaryAddress(
                    AddressMapper.addressToAddressResponse(parent.getPrimaryAddress())
            );
        }

        if (parent.getSecondaryAddress() != null) {
            parentResponse.setSecondaryAddress(
                    AddressMapper.addressToAddressResponse(parent.getSecondaryAddress())
            );
        }
        if (!CollectionUtils.isEmpty(parent.getChildren())) {
            List<ChildSummaryResponse> childResponses = parent.getChildren().stream()
                    .map(child -> {
                        ChildSummaryResponse response = new ChildSummaryResponse();
                        response.setId(child.getId());
                        response.setLastName(child.getLastName());
                        response.setFirstName(child.getFirstName());
                        response.setEmail(child.getEmail());
                        return response;
                    })
                    .collect(Collectors.toList());

            parentResponse.setChildren(childResponses);

        }
        return parentResponse;
    }
    public static ParentSummaryResponse entityToSummaryResponse(Parent parent) {
        if (parent == null) {
            return null;
        }
        ParentSummaryResponse parentResponse = new ParentSummaryResponse();
        parentResponse.setId(parent.getId());
        parentResponse.setLastName(parent.getLastName());
        parentResponse.setFirstName(parent.getFirstName());
        parentResponse.setEmail(parent.getEmail());
        return parentResponse;
    }
    public static ParentStripeResponse entityToStripeResponse(Parent parent) {
        if (parent == null) {
            return null;
        }
        ParentStripeResponse parentResponse = new ParentStripeResponse();
        parentResponse.setId(parent.getId());
        parentResponse.setLastName(parent.getLastName());
        parentResponse.setFirstName(parent.getFirstName());
        parentResponse.setEmail(parent.getEmail());
        parentResponse.setGatewayCustomerId(parent.getGatewayCustomerId());
        return parentResponse;
    }
}
