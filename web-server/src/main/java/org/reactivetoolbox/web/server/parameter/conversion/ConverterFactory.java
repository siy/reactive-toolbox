package org.reactivetoolbox.web.server.parameter.conversion;

import java.util.Optional;

public interface ConverterFactory {

    <T> Converter<Optional<T>> get(Class<T> type, String name);

    default <T> Converter<Optional<T>> get(final Class<T> type) {
        return get(type, null);
    }
}
