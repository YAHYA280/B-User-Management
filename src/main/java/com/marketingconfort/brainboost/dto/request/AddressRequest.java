package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressRequest {
    private String country;
    private String complimentaryAddress;
    private String city;
    private String fullAddress;
    private String zipCode;
}
