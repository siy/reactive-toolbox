package org.reactivetoolbox.web.server.parameter.conversion.type;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.functional.Option;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ParameterTypeTest {
    @Test
    void shouldCreateParameterTypeForSimpleClass() {
        ParameterType.represent(String.class)
                     .onSuccess(type -> assertEquals(String.class, type.base()))
                     .onSuccess(type -> assertEquals(Option.empty(), type.element()))
                     .onFailure((failure) -> fail());
    }

    @Test
    void shouldCreateParameterTypeToken() {
        ParameterType.represent(new TypeToken<List<String>>() {})
                     .onSuccess(type -> assertEquals(List.class, type.base()))
                     .onSuccess(type -> assertEquals(Option.of(String.class), type.element()))
                     .onFailure((failure) -> fail());
    }

    @Test
    void shouldCreateKeyForArrayType() {
        ParameterType.represent(String[].class)
                     .onSuccess(type -> assertEquals(String[].class, type.base()))
                     .onFailure((failure) -> fail());
    }

    @Test
    void shouldCreateKeyForWildcardType() {
        ParameterType.represent(new TypeToken<List<?>>() {})
                     .onSuccess(type -> assertEquals(List.class, type.base()))
                     .onSuccess(type -> assertEquals(Option.empty(), type.element()))
                     .onFailure((failure) -> fail());
    }

    @Test
    void shouldFailOnNullType() {
        ParameterType.represent((Type) null)
                     .onSuccess(type -> fail());
    }

    @Test
    void shouldCreateKeyForGenericArrayType() {
        ParameterType.represent(new TypeToken<List<String[]>>() {})
                     .onSuccess(type -> assertEquals(List.class, type.base()))
                     .onSuccess(type -> assertEquals(Option.of(String[].class), type.element()))
                     .onFailure((failure) -> fail());
    }
}