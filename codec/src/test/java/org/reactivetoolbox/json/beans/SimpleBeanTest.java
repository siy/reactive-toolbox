package org.reactivetoolbox.json.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBeanTest {
    @Test
    void beanCanBeSerialized() {
        final SimpleBean v = SimpleBean.of("one", "two");
        assertEquals("{\"name\":\"one\",\"value\":\"two\"}", SimpleBean.serialize(v));
    }
}