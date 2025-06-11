package com.marketingconfort.brainboost.constants;

public class MessageConstants {

    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String ROLE_NAME_NOT_NULL = "Role name cannot be null";
    public static final String ROLE_ALREADY_EXISTS = "Role already exists in both the database and Keycloak";
    public static final String ADMIN_ROLE_LIST_NOT_NULL = "Role list cannot be empty";
    public static final String ROLE_NAME_ALREADY_EXISTS = "Role already exists with the same name";
    public static final String ROLE_DELETE_ERROR_ASSIGNED_TO_ADMINS = "Cannot delete role: it is assigned to one or more admins.";
    public static final String EMAIL_EMPTY = "Email cannot be empty";
    public static final String FIRST_NAME_EMPTY = "First name cannot be empty";
    public static final String LAST_NAME_EMPTY = "Last name cannot be empty";

    public static final String PASSWORD_TOO_SHORT = "Le mot de passe doit contenir au moins 8 caractères.";

    public static final String USER_EXISTS_EMAIL = "Email already exists";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_SOFT_DELETE_ERROR = "User is already deleted. Current status: ";
    public static final String BLOCK_DELETED_USER_ERROR = "Cannot block a deleted user. Current status: ";
    public static final String SUSPEND_DELETED_USER_ERROR = "Cannot suspend a deleted user. Current status: ";
    public static final String REACTIVATE_DELETED_USER_ERROR = "Cannot reactivate a deleted user. Current status: ";
    public static final String PARENT_NOT_FOUND = "Parent not found";
    public static final String PARENT_DELETED = "Cannot add a child to a deleted parent.";
    public static final String ACCESS_DENIED = "Parents, and children do not have access to the back office.";
    public static final String ACCESS_DENIED_AP = "Admin, and children do not have access to the parent application.";
    public static final String ACCOUNT_BLOCKED = "Your account has been blocked.";
    public static final String ACCOUNT_SUSPENDED = "Your account has been suspended.";
    public static final String ACCOUNT_DELETED = "Your account has been deleted.";
    public static final String USER_STATUS_NOT_RECOGNIZED = "User status not recognized.";
}