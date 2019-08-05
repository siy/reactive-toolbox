package org.reactivetoolbox.json.beans;

import lombok.Data;

@Data(staticConstructor = "of")
public class SimpleBean {
//    static {
//        registerType(codec(SimpleBean.class,
//                           (val) -> encode(pair("name", val.name),
//                                           pair("value", val.value)),
//                           (obj) -> Option.of(SimpleBean.of(
//                               obj.get("name").map(decoder(String.class).get()).get(),
//                               obj.get("value").map(decoder(String.class).get()).get())));
//    }

    private final String name;
    private final String value;
}
