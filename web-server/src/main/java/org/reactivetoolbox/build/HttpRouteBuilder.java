package org.reactivetoolbox.build;

import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder1;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder2;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder3;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.web.server.AuthenticationError;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class HttpRouteBuilder {
    private final HttpMethod method;
    private Path path;
    private FN1 handler;
    private FN1<Either<AuthenticationError, RequestContext>, RequestContext> authHandler = context -> Either.success(context);

    private HttpRouteBuilder(final HttpMethod method) {
        this.method = method;
    }

    public static HttpRouteBuilder routeFor(final HttpMethod method) {
        return new HttpRouteBuilder(method);
    }

    public <R, T> HttpRouteBuilder withHandler(FN1<Promise<R>, T> handler) {
        this.handler = handler;
        return this;
    }

    public HttpRouteBuilder withAuthHandler(final FN1<Either<AuthenticationError, RequestContext>, RequestContext> authenticationVerifier) {
        this.authHandler = authenticationVerifier;
        return this;
    }

    public HttpRouteBuilder withPath(final String path) {
        this.path = Path.of(path);
        return this;
    }

    public <T1> ParameterBuilder1<T1> with(final Parameter<T1> param1) {
        return ParameterBuilder.of(this, param1);
    }

    public <T1, T2> ParameterBuilder2<T1, T2> with(final Parameter<T1> param1, final Parameter<T2> param2) {
        return ParameterBuilder.of(this, param1, param2);
    }

    public <T1, T2, T3> ParameterBuilder3<T1, T2, T3> with(final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3) {
        return ParameterBuilder.of(this, param1, param2, param3);
    }

    public Route<RequestContext> build() {
        //var fullHandler =

        return null;
    }
}
