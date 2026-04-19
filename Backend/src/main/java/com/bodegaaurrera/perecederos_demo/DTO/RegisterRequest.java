package com.bodegaaurrera.perecederos_demo.DTO;

import com.bodegaaurrera.perecederos_demo.Enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}