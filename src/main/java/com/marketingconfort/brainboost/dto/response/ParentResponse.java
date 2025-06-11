package com.marketingconfort.brainboost.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParentResponse extends BaseUserResponse {
    private List<ChildSummaryResponse> children;
}
