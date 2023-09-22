package com.apigateway.utils;

import com.apigateway.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    public static <T> ResponseEntity<ResponseDTO<T>> buildResponse(T data, HttpStatus httpStatus) {
        ResponseDTO<T> response = new ResponseDTO<>(data);
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> buildErrorResponse(
            String errorMsg, HttpStatus httpStatus) {
        ResponseEntity<ResponseDTO<T>> response = buildResponse(null, httpStatus);
        response.getBody().setErrorMessage(errorMsg);
        return response;
    }

}