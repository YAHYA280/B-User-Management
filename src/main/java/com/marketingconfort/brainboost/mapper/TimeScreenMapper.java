package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.request.TimeScreenRequest;
import com.marketingconfort.brainboost.dto.response.TimeScreenResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.Child;
import com.marketingconfort.brainboost_common.usermanagement.models.TimeScreen;

public class TimeScreenMapper {
    public static TimeScreen timeScreenRequestToTimeScreen(TimeScreenRequest request) {
        if (request == null) {
            return null;
        }
        TimeScreen timeScreen = new TimeScreen();
        timeScreen.setDuration(request.getDuration());
        Child child = new Child();
        child.setId(request.getChildId());
        timeScreen.setChild(child);
        return timeScreen;
    }
    public static TimeScreenResponse timeScreenToTimeScreenResponse(TimeScreen timeScreen) {
        if (timeScreen == null) {
            return null;
        }
        TimeScreenResponse response = new TimeScreenResponse();
        response.setId(timeScreen.getId());
        response.setDuration(timeScreen.getDuration());
        response.setCreatedAt(timeScreen.getCreatedAt());
        if (timeScreen.getChild() != null) {
            response.setChildId(timeScreen.getChild().getId());
        }
        return response;
    }


}
