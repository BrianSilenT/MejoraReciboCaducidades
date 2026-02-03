package com.bodegaaurrera.perecederos_demo.Config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(IllegalArgumentException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Validación fallida");
        errorBody.put("detalle", ex.getMessage());
        errorBody.put("timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErroresGenericos(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Error interno");
        errorBody.put("detalle", ex.getMessage());
        errorBody.put("timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}