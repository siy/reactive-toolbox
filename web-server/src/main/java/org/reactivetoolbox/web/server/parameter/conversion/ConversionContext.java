package org.reactivetoolbox.web.server.parameter.conversion;

public interface ConversionContext {
    <T> ValueConverter<T> valueConverter(Class<T> type);
}
