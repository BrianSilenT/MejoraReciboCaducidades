package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Division {
    PERECEDEROS,
    NO_PERECEDEROS;

    @JsonCreator
    public static Division fromValue(String value) {
        return Division.valueOf(value.toUpperCase());
    }

}
