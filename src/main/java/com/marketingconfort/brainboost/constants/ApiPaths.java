package com.marketingconfort.brainboost.constants;

public class ApiPaths {
    public static final String BASE_URL = "/api/user-management";

    public static final String USER_URL = BASE_URL + "/users";
    public static final String ADMIN_LOGIN = "/admin/login";
    public static final String PARENT_LOGIN = "/parent/login";

    public static final String ADMIN_URL = BASE_URL + "/admins";
    public static final String CURRENT_USER = "/current-user";

    public static final String PARENT_URL = BASE_URL + "/parents";
    public static final String UPDATE_GATEWAY_CUSTOMERID = "{id}/gateway-customer";
    public static final String SEARCH_PARENT_IDS = "/search/ids";

    public static final String CHILD_URL = BASE_URL + "/children";
    public static final String SOFT_DELETE_URL_BY_PARENT = "/parent/{id}/soft-delete";
    public static final String BLOCK_BY_ID_BY_PARENT = "parent/{id}/block";
    public static final String SUSPEND_BY_ID_BY_PARENT = "/parent/{id}/suspend";
    public static final String REACTIVATE_BY_ID_BY_PARENT = "/parent/{id}/reactivate";

    public static final String ROLE_URL = BASE_URL + "/roles";
    public static final String ROLE_IS_ASSIGNED_URL = "{id}/is-assigned";

    public static final String GET_BY_ID = "/id/{id}";
    public static final String GET_BY_NAME = "/name/{name}";
    public static final String GET_BY_CUSTOMERID = "/customer-id/{customerid}";
    public static final String UPDATE_BY_ID = "/{id}";
    public static final String UPDATE_ADMIN_ROLE_BY_ID = "/{id}/role";
    public static final String DELETE_BY_ID = "/{id}";
    public static final String SOFT_DELETE_URL = "{id}/soft-delete";
    public static final String BLOCK_BY_ID = "/{id}/block";
    public static final String SUSPEND_BY_ID = "/{id}/suspend";
    public static final String REACTIVATE_BY_ID = "/{id}/reactivate";
}
