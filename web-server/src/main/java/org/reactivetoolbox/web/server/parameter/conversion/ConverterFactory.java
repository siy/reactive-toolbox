package org.reactivetoolbox.web.server.parameter.conversion;

public interface ConverterFactory {

    <T> Converter<T> get(Class<T> type, String name);

    default <T> Converter<T> get(final Class<T> type) {
        return get(type, null);
    }
}
