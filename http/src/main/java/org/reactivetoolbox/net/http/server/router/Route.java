package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.net.http.ContentType;
import org.reactivetoolbox.net.http.server.NativeBuffer;
import org.reactivetoolbox.net.http.server.RequestContext;

import java.util.StringJoiner;
import java.util.stream.Stream;

//TODO: add documentation to route
public interface Route extends Prefixed<Route>, RouteSource {
    FN1<Promise<NativeBuffer>, RequestContext> handler();

    Path path();

    ContentType inputType();
    ContentType outputType();

    @Override
    default Stream<Route> stream() {
        return Stream.of(this);
    }

    static Route route(final Path path,
                       final FN1<Promise<NativeBuffer>, RequestContext> handler,
                       final ContentType inputType,
                       final ContentType outputType) {
        return new Route() {
            @Override
            public FN1<Promise<NativeBuffer>, RequestContext> handler() {
                return handler;
            }

            @Override
            public Path path() {
                return path;
            }

            @Override
            public ContentType inputType() {
                return inputType;
            }

            @Override
            public ContentType outputType() {
                return outputType;
            }

            @Override
            public Route prefix(final String prefix) {
                return route(path.prefix(prefix), handler, inputType, outputType);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Route(", ")")
                        .add(path.toString())
                        .add("\"" + inputType.get() + "\"")
                        .add("\"" + outputType.get() + "\"")
                        .toString();
            }
        };
    }
}
