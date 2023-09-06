package com.authservice.enumeration;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public enum RoleEnum {

    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    public String getAuthority() {
        return value;
    }

}