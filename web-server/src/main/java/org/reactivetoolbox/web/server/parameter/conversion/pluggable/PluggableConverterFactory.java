package org.reactivetoolbox.web.server.parameter.conversion.pluggable;

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.HeaderName;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;

import java.nio.ByteBuffer;
import java.util.ServiceLoader;

public class PluggableConverterFactory implements ConverterFactory {
    private final ConverterFactory delegate;

    private PluggableConverterFactory(final ConverterFactory delegate) {
        this.delegate = delegate;
    }

    public static ConverterFactory load() {
        return new PluggableConverterFactory(LazyLoader.FACTORY);
    }

    @Override
    public <T> Converter<Option<T>> getParameterConverter(final Class<T> type, final String name) {
        return delegate.getParameterConverter(type, name);
    }

    @Override
    public <T> Converter<Option<T>> getHeaderConverter(final Class<T> type, final HeaderName name) {
        return delegate.getHeaderConverter(type, name);
    }

    @Override
    public <T> Converter<Option<T>> getHeaderConverter(final Class<T> type,
                                                       final HeaderName name,
                                                       final AuthHeader headerType) {
        return delegate.getHeaderConverter(type, name, headerType);
    }

    @Override
    public <T> Converter<Option<T>> getBodyValueConverter(final Class<T> type, final String name) {
        return delegate.getBodyValueConverter(type, name);
    }

    @Override
    public <T> Converter<Option<T>> getContextConverter(final Class<T> type) {
        return delegate.getContextConverter(type);
    }

    @Override
    public <T> FN1<ByteBuffer[], Object> getResultSerializer() {
        return delegate.getResultSerializer();
    }

    private static class LazyLoader {
        private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
                                                                     .findFirst()
                                                                     .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));
    }
}
