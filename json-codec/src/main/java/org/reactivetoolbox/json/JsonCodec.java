package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Pair;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.functional.Either.success;
import static org.reactivetoolbox.json.TypeConversionError.UNKNOWN_TYPE;

//TODO: finish it
public class JsonCodec {
    private static final Map<Class<?>, TypeCodec<?>> REGISTRY = new ConcurrentHashMap<>();

    static {
        REGISTRY.put(Option.class, TypeCodec.of(Option.class, CoreCodec::optionEncoder, CoreCodec::optionDecoder));
        REGISTRY.put(Optional.class, TypeCodec.of(Optional.class, CoreCodec::optionalEncoder, CoreCodec::optionalDecoder));
        REGISTRY.put(String.class, TypeCodec.of(String.class, CoreCodec::stringEncoder, CoreCodec::stringDecoder));
        REGISTRY.put(Boolean.class, TypeCodec.of(Boolean.class, CoreCodec::booleanEncoder, CoreCodec::booleanDecoder));
        REGISTRY.put(UUID.class, TypeCodec.of(UUID.class, CoreCodec::uuidEncoder, CoreCodec::uuidDecoder));

        REGISTRY.put(Integer.class, TypeCodec.of(Integer.class, CoreCodec::integerEncoder, CoreCodec::integerDecoder));
        REGISTRY.put(Long.class, TypeCodec.of(Long.class, CoreCodec::longEncoder, CoreCodec::longDecoder));

        REGISTRY.put(Float.class, TypeCodec.of(Float.class, CoreCodec::floatEncoder, CoreCodec::floatDecoder));
        REGISTRY.put(Double.class, TypeCodec.of(Double.class, CoreCodec::doubleEncoder, CoreCodec::doubleDecoder));
        REGISTRY.put(BigDecimal.class, TypeCodec.of(BigDecimal.class, CoreCodec::bigDecimalEncoder, CoreCodec::bigDecimalDecoder));

        REGISTRY.put(LocalDateTime.class, TypeCodec.of(LocalDateTime.class, CoreCodec::localDateTimeEncoder, CoreCodec::localDateTimeDecoder));
        REGISTRY.put(ZonedDateTime.class, TypeCodec.of(ZonedDateTime.class, CoreCodec::zonedDateTimeEncoder, CoreCodec::zonedDateTimeDecoder));
        REGISTRY.put(Instant.class, TypeCodec.of(Instant.class, CoreCodec::instantEncoder, CoreCodec::instantDecoder));
    }

    public static <T> void registerType(final Class<T> type, final TypeCodec<T> codec) {
        REGISTRY.put(type, codec);
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<TypeCodec<T>> registerType(final Class<T> type) {
        return Option.of((TypeCodec<T>) REGISTRY.get(type));
    }

    public <T> Either<? extends BaseError, Option<T>> decode(final Class<T> type, final Input input) {
        if (!input.hasNext()) {
            return success(Option.empty());
        }

        if (Collection.class.isAssignableFrom(type)) {
            if (input.next().type() != TokenType.ARRAY_START) {
                //Expect start of array
            }

            while (input.next().type() != TokenType.ARRAY_END) {
                // Read array
            }

            return null;
        }

        return null;

//        switch (input.next()) {
//            case ARRAY_START:
//                break;
//                case ARRAY_END
//        }
    }

    //TODO: support arrays??
    @SuppressWarnings("unchecked")
    public <T> Either<? extends BaseError, String> encode(final T value) {
        if (value == null) {
            return success("null");
        }

        if (value instanceof Collection) {
            return serialize((Collection<?>) value, "[", "]",
                             (joiner, element) -> encode(element).onSuccess(joiner::add));
        }

        if (value instanceof Map) {
            return serialize(((Map<?, ?>) value).entrySet(), "[", "]",
                             (joiner, entry) -> encode(entry.getKey()).onSuccess(s -> joiner.add(s).add("\":"))
                                                                      .flatMap(v -> encode(entry.getKey()))
                                                                      .onSuccess(joiner::add));
        }

        final Option<Either<? extends BaseError, String>>
                result = Option.of((TypeCodec<T>) REGISTRY.get(value.getClass()))
                               .map(TypeCodec::encoder)
                               .map(encoder -> encoder.apply(value));

        return result.isPresent() ? result.get() : UNKNOWN_TYPE.asFailure();
    }

    public Either<? extends BaseError, String> serializeObject(final List<Pair<String, Supplier<?>>> fields) {
        return serialize(fields, "{", "}",
                         (joiner, pair) -> encode(pair.left()).onSuccess(s -> joiner.add(s).add("\":"))
                                                              .flatMap(v -> encode(pair.right().get()))
                                                              .onSuccess(joiner::add));
    }

    private <T> Either<? extends BaseError, String> serialize(final Collection<T> collection,
                                                              final String prefix,
                                                              final String suffix,
                                                              final FN2<Either<? extends BaseError, String>, StringJoiner, T> handler) {
        final StringJoiner joiner = new StringJoiner(",", prefix, suffix);

        for(final T element : collection) {
            final var result = handler.apply(joiner, element);

            if (result.isFailure()) {
                return result;
            }
        }
        return success(joiner.toString());
    }

}
