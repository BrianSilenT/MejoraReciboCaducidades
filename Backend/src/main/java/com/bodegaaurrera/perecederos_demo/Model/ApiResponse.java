package com.bodegaaurrera.perecederos_demo.Model;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponse<T> {
    private int total;
    private String fecha;
    private T data;

    public ApiResponse(T data) {
        this.data = data;
        this.total = (data instanceof List) ? ((List<?>) data).size() : 1;
        this.fecha = LocalDateTime.now().toString();
    }

    public int getTotal() {
        return total;
    }

    public String getFecha() {
        return fecha;
    }

    public T getData() {
        return data;
    }
}
