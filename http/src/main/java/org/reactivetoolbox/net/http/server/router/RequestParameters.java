package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.codec.Parameter;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.TypeToken;
import org.reactivetoolbox.net.http.server.ParsingContext;
import org.reactivetoolbox.net.http.server.RequestContext;

import static org.reactivetoolbox.codec.Parameter.*;

public interface RequestParameters {
    static <T> RequestParameter<Option<T>> assembly(final FN1<ParsingContext, RequestContext> contextFn, final Parameter<Option<T>> extractFn) {
        return context -> extractFn.apply(contextFn.apply(context));
    }

    static <T> RequestParameter<Option<T>> inPath(final Class<T> type, final String name) {
        return assembly(RequestContext::pathContext, named(type, name));
    }

    static <T> RequestParameter<Option<T>> inPath(final TypeToken<T> type, final String name) {
        return assembly(RequestContext::pathContext, named(type, name));
    }

    static <T> RequestParameter<Option<T>> inQuery(final Class<T> type, final String name) {
        return assembly(RequestContext::queryContext, named(type, name));
    }

    static <T> RequestParameter<Option<T>> inQuery(final TypeToken<T> type, final String name) {
        return assembly(RequestContext::queryContext, named(type, name));
    }

    static <T> RequestParameter<Option<T>> inHeader(final Class<T> type, final String name) {
        return assembly(RequestContext::headerContext, named(type, name));
    }

    //TODO: check naming Authentication vs Authorization
    static RequestParameter<Option<Authentication>> inAuthHeader(final AuthType type) {
        return assembly(RequestContext::headerContext, named(Authentication.class, type.headerName()));
    }

    static <T> RequestParameter<Option<T>> inHeader(final TypeToken<T> type, final String name) {
        return assembly(RequestContext::headerContext, named(type, name));
    }

    static <T> RequestParameter<Option<T>> inBody(final Class<T> type) {
        return assembly(RequestContext::bodyContext, anonymous(type));
    }

    static <T> RequestParameter<Option<T>> inHeader(final TypeToken<T> type) {
        return assembly(RequestContext::bodyContext, anonymous(type));
    }
}
