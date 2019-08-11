package org.reactivetoolbox.json.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBeanTest {
    @Test
    void beanCanBeSerialized() {
        assertEquals("{\"name\":\"one\",\"value\":\"two\"}", SimpleBean.serialize(SimpleBean.of("one", "two")));
    }
}