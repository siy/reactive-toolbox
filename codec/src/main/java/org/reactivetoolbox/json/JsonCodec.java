package org.reactivetoolbox.json;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO: JavaDoc
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
