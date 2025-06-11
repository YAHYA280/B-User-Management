package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost_common.usermanagement.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChildResponse extends BaseUserResponse{
    private Duration totalTimeSpent;
    private ParentSummaryResponse parent;
    private List<TimeScreenResponse> timeScreens;
    private UserStatus status;
}
