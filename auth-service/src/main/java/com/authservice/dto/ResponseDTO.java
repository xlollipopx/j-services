package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> extends ErrorDTO{
    private T data;

    public ResponseDTO(T data) {
        super(null);
        this.data = data;
    }
}
