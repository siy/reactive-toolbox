package org.reactivetoolbox.json.beans;

import org.reactivetoolbox.json.StringAssembler;
import org.reactivetoolbox.value.validation.Is;

import static org.reactivetoolbox.json.JsonCodec.register;
import static org.reactivetoolbox.json.ObjectAssembler.field;
import static org.reactivetoolbox.json.ObjectAssembler.fields;

public class SimpleBean {
    static {
        register(SimpleBean.class, SimpleBean::serialize);
        register(SimpleBean.class, fields(SimpleBean.class,
                                          field(String.class, "name").and(Is::notNull),
                                          field(String.class, "value").and(Is::notNull))
                .deserializer());
    }

    private final String name;
    private final String value;

    private SimpleBean(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static SimpleBean of(final String name, final String value) {
        return new SimpleBean(name, value);
    }

    public static String serialize(final SimpleBean v) {
        return StringAssembler.of('{', '}')
                              .quoted("name", v.name)
                              .quoted("value", v.value)
                              .toString();
    }
}
