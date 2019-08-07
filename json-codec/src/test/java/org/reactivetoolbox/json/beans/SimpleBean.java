package org.reactivetoolbox.json.beans;

import org.reactivetoolbox.json.StringAssembler;

import java.util.List;

import static org.reactivetoolbox.json.Field.field;
import static org.reactivetoolbox.json.JsonCodec.register;

public class SimpleBean {
    static {
        register(SimpleBean.class, SimpleBean::serialize);

        final var fields = List.of(field("name", String.class).and(),
                                   field("value", String.class).required());

        //List form
        register(SimpleBean.class, fields, (obj) -> SimpleBean.of((String) obj.get(0),
                                                                  (String) obj.get(1)));

        //Tuple form
        register(SimpleBean.class, fields(field("name", String.class).required(),
                                          field("value", String.class).required()),
                 (obj) -> obj.map(SimpleBean::of));

//        register(SimpleBean.class,
//                 field("name", String.class/*, SimpleBean::getName).and(required())*/),
//                 field("value", String.class/*, SimpleBean::getValue).and(required())*/));
//        register(SimpleBean.class, (SimpleBean v) -> );
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
