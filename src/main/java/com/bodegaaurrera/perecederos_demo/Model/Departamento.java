package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Departamento {
    FRUTAS,
    VERDURAS,
    LACTEOS,
    CARNES,
    EMBUTIDOS,
    FARMACIA;

    @JsonCreator
    public static Departamento fromValue(String value) {
        return Departamento.valueOf(value.toUpperCase());
    }
}