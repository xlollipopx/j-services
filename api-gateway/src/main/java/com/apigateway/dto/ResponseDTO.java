package com.apigateway.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ResponseDTO<T> extends ErrorDTO{
    private T data;

    public ResponseDTO(T data) {
        super(null);
        this.data = data;
    }
}
