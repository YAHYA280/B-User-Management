package com.marketingconfort.brainboost.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String country;
    private String complimentaryAddress; // On corrige ici le nom
    private String city;
    private String fullAddress;
    private String zipCode;
    private LocalDateTime createdAt;
}
