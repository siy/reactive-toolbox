package org.reactivetoolbox.build;

import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder0;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder1;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder2;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder3;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder4;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder5;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder6;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder7;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder8;
import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder9;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class HttpRouteBuilder {
    private final HttpMethod method;
    private Path path;
    private FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, RequestContext>
            handler = (context) -> Either.success(Promises.fulfilled(Either.success("{}")));
    private FN1<Either<? extends BaseError, RequestContext>, RequestContext> authHandler =
            Either::success;

    private HttpRouteBuilder(final HttpMethod method) {
        this.method = method;
    }

    public static HttpRouteBuilder routeFor(final HttpMethod method) {
        return new HttpRouteBuilder(method);
    }

    public HttpRouteBuilder withHandler(FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, RequestContext> handler) {
        this.handler = handler;
        return this;
    }

    public HttpRouteBuilder ensure(final FN1<Either<? extends BaseError, RequestContext>, RequestContext> authenticationVerifier) {
        this.authHandler = authenticationVerifier;
        return this;
    }

    public HttpRouteBuilder to(final String path) {
        this.path = Path.of(path, method);
        return this;
    }

    public <T1> ParameterBuilder1<T1> findParameters(final Parameter<T1> param1) {
        return ParameterBuilder.of(this, param1);
    }

    public <T1, T2> ParameterBuilder2<T1, T2> findParameters(final Parameter<T1> param1,
                                                             final Parameter<T2> param2) {
        return ParameterBuilder.of(this, param1, param2);
    }

    public <T1, T2, T3> ParameterBuilder3<T1, T2, T3> findParameters(final Parameter<T1> param1,
                                                                     final Parameter<T2> param2,
                                                                     final Parameter<T3> param3) {
        return ParameterBuilder.of(this, param1, param2, param3);
    }

    public <T1, T2, T3, T4> ParameterBuilder4<T1, T2, T3, T4> findParameters(final Parameter<T1> param1,
                                                                             final Parameter<T2> param2,
                                                                             final Parameter<T3> param3,
                                                                             final Parameter<T4> param4) {
        return ParameterBuilder.of(this, param1, param2, param3, param4);
    }

    public <T1, T2, T3, T4, T5> ParameterBuilder5<T1, T2, T3, T4, T5> findParameters(final Parameter<T1> param1,
                                                                                     final Parameter<T2> param2,
                                                                                     final Parameter<T3> param3,
                                                                                     final Parameter<T4> param4,
                                                                                     final Parameter<T5> param5) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5);
    }

    public <T1, T2, T3, T4, T5, T6> ParameterBuilder6<T1, T2, T3, T4, T5, T6> findParameters(final Parameter<T1> param1,
                                                                                             final Parameter<T2> param2,
                                                                                             final Parameter<T3> param3,
                                                                                             final Parameter<T4> param4,
                                                                                             final Parameter<T5> param5,
                                                                                             final Parameter<T6> param6) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> findParameters(
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6, param7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> findParameters(
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7,
            final Parameter<T8> param8) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6, param7,
                                   param8);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> findParameters(
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7,
            final Parameter<T8> param8,
            final Parameter<T9> param9) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6, param7,
                                   param8, param9);
    }

    public ParameterBuilder0 withoutParameters() {
        return ParameterBuilder.of(this);
    }

    public Route<RequestContext> build() {
        return Route.of(path, handler);
    }
}
