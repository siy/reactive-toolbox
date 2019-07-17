package org.reactivetoolbox.web.server.parameter.conversion;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;

public interface ConverterFactory {

    <T> Converter<Option<T>> getParameterConverter(Class<T> type, String name);

    <T> Converter<Option<T>> getHeaderConverter(Class<T> type, String name);

    <T> Converter<Option<T>> getHeaderConverter(Class<T> type, String name, AuthHeader headerType);

    <T> Converter<Option<T>> getBodyValueConverter(final Class<T> type);

    <T> Converter<Option<T>> getContextConverter(final Class<T> type);
}
