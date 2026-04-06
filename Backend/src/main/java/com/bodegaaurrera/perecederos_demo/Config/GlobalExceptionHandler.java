package com.bodegaaurrera.perecederos_demo.Config;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Manejo de excepciones genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarExcepcionGeneral(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());

        ApiResponse<Map<String, String>> respuesta = new ApiResponse<>(error);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ✅ Manejo de validaciones (ej. @Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Map<String, String>> respuesta = new ApiResponse<>(errores);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // ✅ Manejo de IllegalArgumentException (ej. enums inválidos)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        ApiResponse<Map<String, String>> respuesta = new ApiResponse<>(error);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}