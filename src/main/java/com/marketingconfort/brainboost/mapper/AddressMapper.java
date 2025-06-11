package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.AddressRequest;
import com.marketingconfort.brainboost.dto.response.AddressResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.Address;

public class AddressMapper {
    public static Address addressRequestToAddress(AddressRequest addressRequest) {
        if (addressRequest == null) {
            return null;
        }

        Address address = new Address();
        address.setCountry(addressRequest.getCountry());
        address.setComplimentaryAddress(addressRequest.getComplimentaryAddress());
        address.setCity(addressRequest.getCity());
        address.setFullAddress(addressRequest.getFullAddress());
        address.setZipCode(addressRequest.getZipCode());

        return address;
    }

    public static AddressResponse addressToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setCountry(address.getCountry());
        response.setComplimentaryAddress(address.getComplimentaryAddress());
        response.setCity(address.getCity());
        response.setFullAddress(address.getFullAddress());
        response.setZipCode(address.getZipCode());
        response.setCreatedAt(address.getCreatedAt());
        return response;
    }

}
