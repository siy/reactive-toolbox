package org.reactivetoolbox.json;

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JsonCodec {
    private static final Map<Class<?>, FN1<String, ?>> SERIALIZERS = new ConcurrentHashMap<>();

    static {
        SERIALIZERS.put(Option.class, (Option<Object> v)-> serialize(v.get()).get());
        SERIALIZERS.put(Optional.class, (Optional<?> v) -> serialize(v.orElse(null)).get());
        SERIALIZERS.put(String.class, v -> StringAssembler.of('"', '"').plain((String) v).toString());
        SERIALIZERS.put(Boolean.class, Object::toString);
        SERIALIZERS.put(UUID.class, v -> StringAssembler.of('"', '"').plain(v.toString()).toString());
        SERIALIZERS.put(Integer.class, Object::toString);
        SERIALIZERS.put(Long.class, Object::toString);
        SERIALIZERS.put(Float.class, Object::toString);
        SERIALIZERS.put(Double.class, Object::toString);
        SERIALIZERS.put(BigDecimal.class, Object::toString);
        //TODO: finish it
//        SERIALIZERS.put(LocalDateTime.class, );
//        SERIALIZERS.put(ZonedDateTime.class, );
//        SERIALIZERS.put(Instant.class, );
        //TODO: add collections and map
    }

    private JsonCodec() {}

    public static <T> void register(final Class<T> type, final FN1<String, ? super T> serializer) {
        SERIALIZERS.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<String> serialize(final T value) {
        return Option.of(value)
                     .map(Object::getClass)
                     .flatMap(cls -> Option.of((FN1<String, T>) (FN1) SERIALIZERS.get(cls))
                                           .map(fn -> fn.apply(value)))
                     .or(() -> Option.of("null"));
    }
}
