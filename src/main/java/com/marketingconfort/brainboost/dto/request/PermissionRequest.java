package com.marketingconfort.brainboost.dto.request;

import com.marketingconfort.brainboost_common.usermanagement.enums.PermissionType;
import com.marketingconfort.brainboost_common.usermanagement.enums.SubModule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PermissionRequest {
    private SubModule subModule;
    private PermissionType permissionType;
}
