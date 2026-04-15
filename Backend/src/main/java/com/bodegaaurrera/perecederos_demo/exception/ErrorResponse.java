package com.bodegaaurrera.perecederos_demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String detalle;
    private String timestamp;
}