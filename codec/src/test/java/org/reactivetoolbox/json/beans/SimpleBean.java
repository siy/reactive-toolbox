package org.reactivetoolbox.json.beans;

import org.reactivetoolbox.value.validation.Is;

import static org.reactivetoolbox.json.JsonCodec.addDeserializer;
import static org.reactivetoolbox.json.JsonCodec.addSerializer;
import static org.reactivetoolbox.json.ObjectAssembler.field;
import static org.reactivetoolbox.json.ObjectAssembler.fields;
import static org.reactivetoolbox.json.StringAssembler.assembleWith;

public class SimpleBean {
    static {
        addSerializer(SimpleBean.class, v -> assembleWith('{', '}')
                .quoted("name", v.name)
                .quoted("value", v.value)
                .toString());
        addDeserializer(SimpleBean.class, fields(SimpleBean.class,
                                                 field(String.class, "name").and(Is::notNull),
                                                 field(String.class, "value").and(Is::notNull))
                .deserializer(SimpleBean::new));
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
        return assembleWith('{', '}')
                .quoted("name", v.name)
                .quoted("value", v.value)
                .toString();
    }
}
