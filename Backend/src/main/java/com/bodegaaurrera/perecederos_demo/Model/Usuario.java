package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Aquí irá el hash de BCrypt

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean activo = true;
}