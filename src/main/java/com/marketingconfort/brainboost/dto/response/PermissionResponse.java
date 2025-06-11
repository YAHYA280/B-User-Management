package com.marketingconfort.brainboost.dto.response;

import com.marketingconfort.brainboost_common.usermanagement.enums.PermissionType;
import com.marketingconfort.brainboost_common.usermanagement.enums.SubModule;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class PermissionResponse {
    private long id;
    private SubModule subModule;
    private PermissionType permissionType;
    private LocalDateTime createdAt;
}
