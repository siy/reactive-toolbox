package org.reactivetoolbox.json.beans;

import org.reactivetoolbox.json.StringAssembler;
import org.reactivetoolbox.value.conversion.To;
import org.reactivetoolbox.value.validation.Is;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.reactivetoolbox.json.JsonCodec.addDeserializer;
import static org.reactivetoolbox.json.JsonCodec.addSerializer;
import static org.reactivetoolbox.json.ObjectAssembler.field;
import static org.reactivetoolbox.json.ObjectAssembler.fields;
import static org.reactivetoolbox.json.ObjectAssembler.list;
import static org.reactivetoolbox.json.StringAssembler.forObject;

public class CollectionBean {
    static {
        addDeserializer(CollectionBean.class,
                        fields(CollectionBean.class,
                               field(String.class, "name").and(Is::notNull),
                               list(Integer.class, "indices").and(Is::notNull),
                               list(UUID.class, "ids").and(To::set).and(Is::notNull))
                                .with(CollectionBean::new));

        addSerializer(CollectionBean.class,
                      (v) -> forObject().quoted("name", v.name)
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

    public String name() {
        return name;
    }

    public CollectionBean name(final String name) {
        return new CollectionBean(name, indices, ids);
    }

    public List<Integer> indices() {
        return List.copyOf(indices);
    }

    public CollectionBean indices(final List<Integer> indices) {
        return new CollectionBean(name, indices, ids);
    }

    public Set<UUID> ids() {
        return Set.copyOf(ids);
    }

    public CollectionBean ids(final Set<UUID> ids) {
        return new CollectionBean(name, indices, ids);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CollectionBean that = (CollectionBean) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(indices, that.indices) &&
               Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, indices, ids);
    }

    @Override
    public String toString() {
        return "CollectionBean{" +
               "name='" + name + '\'' +
               ", indices=" + indices +
               ", ids=" + ids +
               '}';
    }
}
