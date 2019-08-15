package org.reactivetoolbox.json.beans;

import org.reactivetoolbox.json.StringAssembler;
import org.reactivetoolbox.value.conversion.To;
import org.reactivetoolbox.value.validation.Is;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.reactivetoolbox.json.JsonCodec.addDeserializer;
import static org.reactivetoolbox.json.JsonCodec.addSerializer;
import static org.reactivetoolbox.json.ObjectAssembler.field;
import static org.reactivetoolbox.json.ObjectAssembler.fields;
import static org.reactivetoolbox.json.ObjectAssembler.list;

public class CollectionBean {
    static {
        addDeserializer(CollectionBean.class, fields(CollectionBean.class,
                                                     field(String.class, "name").and(Is::notNull),
                                                     list(Integer.class, "indices").and(Is::notNull),
                                                     list(UUID.class, "ids").and(To::set).and(Is::notNull))
                .deserializer(CollectionBean::new));

        addSerializer(CollectionBean.class, (v) -> StringAssembler.assembleWith('{', '}')
                                                                  .quoted("name", v.name)
                                                                  .quoted("indices", v.indices, Object::toString)
                                                                  .quoted("ids", v.ids, StringAssembler::toStringQuoted)
                                                                  .toString());
    }

    private final String name;
    private final List<Integer> indices;
    private final Set<UUID> ids;

    private CollectionBean(final String name, final List<Integer> indices, final Set<UUID> ids) {
        this.name = name;
        this.indices = indices;
        this.ids = ids;
    }

    public static CollectionBean of(final String name, final List<Integer> indices, final Set<UUID> ids) {
        return new CollectionBean(name, indices, ids);
    }
}
