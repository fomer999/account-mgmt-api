package com.lf.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;


public enum UserRole {

    ROLE_CREATE_ACCOUNT("ROLE_CREATE_ACCOUNT"),
    ROLE_UPDATE_ACCOUNT("ROLE_UPDATE_ACCOUNT"),
    ROLE_DELETE_ACCOUNT("ROLE_DELETE_ACCOUNT"),
    ROLE_RETRIEVE_ACCOUNT("ROLE_RETRIEVE_ACCOUNT"),
    ROLE_CREATE_USER("ROLE_CREATE_USER"),
    ROLE_UPDATE_USER("ROLE_UPDATE_USER"),
    ROLE_DELETE_USER("ROLE_DELETE_USER"),
    ROLE_RETRIEVE_USER("ROLE_RETRIEVE_USER"),
    ROLE_RETRIEVE_ROLES("ROLE_RETRIEVE_ROLES"),
    ROLE_CREATE_CONFIGURATION("ROLE_CREATE_CONFIGURATION"),
    ROLE_UPDATE_CONFIGURATION("ROLE_UPDATE_CONFIGURATION"),
    ROLE_DELETE_CONFIGURATION("ROLE_DELETE_CONFIGURATION"),
    ROLE_RETRIEVE_CONFIGURATION("ROLE_RETRIEVE_CONFIGURATION");

    private final String value;
    private final static Map<String, UserRole> CONSTANTS = new HashMap<String, UserRole>();

    static {
        for (UserRole c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private UserRole(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static UserRole fromValue(String value) {
        UserRole constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}