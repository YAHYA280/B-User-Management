package com.marketingconfort.brainboost.mapper;

import com.marketingconfort.brainboost.dto.response.UserResponse;
import com.marketingconfort.brainboost_common.usermanagement.models.User;

public class UserMapper {
    public static UserResponse entityToResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setLastName(user.getLastName());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setEmail(user.getEmail());
        userResponse.setStatus(user.getStatus());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;
    }
}
