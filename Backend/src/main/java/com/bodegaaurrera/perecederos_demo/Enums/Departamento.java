package com.bodegaaurrera.perecederos_demo.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Departamento {
    FRUTAS,
    VERDURAS,
    LACTEOS,
    CARNES,
    EMBUTIDOS,
    MULTIPLE,
    FARMACIA;

    @JsonCreator
    public static Departamento fromValue(String value) {
        return Departamento.valueOf(value.toUpperCase());
    }
}