package com.marketingconfort.brainboost.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentStripeResponse {
    private Long id;
    private String lastName;
    private String firstName;
    private String email;
    private String gatewayCustomerId;
}
