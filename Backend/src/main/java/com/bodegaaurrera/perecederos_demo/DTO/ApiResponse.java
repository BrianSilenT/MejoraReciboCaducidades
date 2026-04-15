package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ApiResponse<T> {
    private int total;
    private String fecha;
    private T data;

    public ApiResponse(T data) {
        this.data = data;
        this.total = (data instanceof List) ? ((List<?>) data).size() : 1;
        this.fecha = LocalDateTime.now().toString();
    }
}
