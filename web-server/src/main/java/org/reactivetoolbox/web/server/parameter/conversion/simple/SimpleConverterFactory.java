package org.reactivetoolbox.web.server.parameter.conversion.simple;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.HeaderName;
import org.reactivetoolbox.web.server.parameter.TypeDescription;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SimpleConverterFactory implements ConverterFactory {
    private static final Map<Class, ValueConverter> VALUE_CONVERTERS = new HashMap<>();

    static {
        VALUE_CONVERTERS.put(String.class, new StringValueConverter());
        VALUE_CONVERTERS.put(Integer.class, new IntegerValueConverter());
        VALUE_CONVERTERS.put(Long.class, new LongValueConverter());
        VALUE_CONVERTERS.put(Double.class, new DoubleValueConverter());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Converter<Option<T>> getPathParameterConverter(final Class<T> type, final String name) {
        return new SimpleConverter<>(new SimpleTypeDescriptor<>(type), new PathExtractor(name), VALUE_CONVERTERS.get(type));
    }

    @Override
    public <T> Converter<Option<T>> getQueryParameterConverter(final Class<T> type, final String name) {
        return null;
    }

    @Override
    public <T> Converter<Option<T>> getHeaderConverter(final Class<T> type, final HeaderName name) {
        return null;
    }

    @Override
    public <T> Converter<Option<T>> getHeaderConverter(final Class<T> type,
                                                       final HeaderName name,
                                                       final AuthHeader headerType) {
        return null;
    }

    @Override
    public <T> Converter<Option<T>> getBodyValueConverter(final Class<T> type, final String name) {
        return null;
    }

    @Override
    public <T> Converter<Option<T>> getContextConverter(final Class<T> type) {
        return null;
    }

    @Override
    public <T> FN1<ByteBuffer[], Object> getResultSerializer() {
        return null;
    }

    private interface Extractor extends FN1<Either<? extends BaseError, Option<String>>, RequestContext> {
    }

    private interface ValueConverter<T> extends FN1<Either<? extends BaseError, Option<T>>, Either<? extends BaseError, Option<String>>> {
    }

    private static class SimpleTypeDescriptor<T> {
        private final Class<T> clazz;

        private SimpleTypeDescriptor(final Class<T> clazz) {
            this.clazz = clazz;
        }

        public Option<TypeDescription> typeDescription() {
            return Option.of(TypeDescription.of(clazz.getSimpleName()));
        }
    }

    private static class SimpleConverter<T> implements Converter<Option<T>> {
        private final SimpleTypeDescriptor<T> descriptor;
        private final Converter<Option<T>> converter;

        private SimpleConverter(final SimpleTypeDescriptor<T> descriptor,
                                final Extractor extractor,
                                final ValueConverter<T> converter) {
            Objects.requireNonNull(converter, "Unable to locate value converter for " + descriptor.typeDescription().get());
            this.descriptor = descriptor;
            this.converter = (context) -> converter.apply(extractor.apply(context));
        }

        @Override
        public Either<? extends BaseError, Option<T>> apply(final RequestContext context) {
            return converter.apply(context);
        }

        @Override
        public Option<TypeDescription> typeDescription() {
            return descriptor.typeDescription();
        }
    }

    private class PathExtractor implements Extractor {
        private final String name;

        public PathExtractor(final String name) {
            this.name = name;
        }

        @Override
        public Either<? extends BaseError, Option<String>> apply(final RequestContext context) {
            return Either.success(context.pathParameter(name));
        }
    }

    private static class StringValueConverter implements ValueConverter<String> {
        @Override
        public Either<? extends BaseError, Option<String>> apply(final Either<? extends BaseError, Option<String>> param1) {
            return param1;
        }
    }

    private static class IntegerValueConverter implements ValueConverter<Integer> {
        private final Function<Option<String>, Either<ConversionError, Option<Integer>>> liftedValueOf =
                Either.lift((Option<String> input) -> input.map(Integer::valueOf))
                      .andThen(val -> val.mapFailure(failure -> ConversionError.NOT_A_NUMBER));

        @Override
        public Either<? extends BaseError, Option<Integer>> apply(final Either<? extends BaseError, Option<String>> param1) {
            return param1.flatMap(liftedValueOf::apply);
        }
    }

    private static class LongValueConverter implements ValueConverter<Long> {
        private final Function<Option<String>, Either<ConversionError, Option<Long>>> liftedValueOf =
                Either.lift((Option<String> input) -> input.map(Long::valueOf))
                      .andThen(val -> val.mapFailure(failure -> ConversionError.NOT_A_NUMBER));

        @Override
        public Either<? extends BaseError, Option<Long>> apply(final Either<? extends BaseError, Option<String>> param1) {
            return param1.flatMap(liftedValueOf::apply);
        }
    }

    private static class DoubleValueConverter implements ValueConverter<Double> {
        private final Function<Option<String>, Either<ConversionError, Option<Double>>> liftedValueOf =
                Either.lift((Option<String> input) -> input.map(Double::valueOf))
                      .andThen(val -> val.mapFailure(failure -> ConversionError.NOT_A_NUMBER));

        @Override
        public Either<? extends BaseError, Option<Double>> apply(final Either<? extends BaseError, Option<String>> param1) {
            return param1.flatMap(liftedValueOf::apply);
        }
    }
}
