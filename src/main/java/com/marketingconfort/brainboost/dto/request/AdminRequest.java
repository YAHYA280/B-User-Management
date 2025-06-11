package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminRequest extends BaseUserRequest{
    private List<Long> roleIds;
}
