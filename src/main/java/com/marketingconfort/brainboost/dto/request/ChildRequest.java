package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;
@Getter
@Setter
public class ChildRequest extends BaseUserRequest {
    private Long parentId;
}
