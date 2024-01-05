package com.apigateway.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthenticationStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    ACCESS_DENIED("Access denied");

    private final String value;
}
