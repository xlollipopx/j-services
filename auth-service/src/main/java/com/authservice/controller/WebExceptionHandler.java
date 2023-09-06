package com.authservice.controller;


import com.authservice.dto.ErrorDTO;
import com.authservice.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.authservice.controller.ControllerUtils.buildErrorResponse;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<ErrorDTO>> internalServerError(Exception e) {
        log.error(e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<ErrorDTO>> badCredentialsError(Exception e) {
        log.error(e.getMessage());
        return buildErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
}
