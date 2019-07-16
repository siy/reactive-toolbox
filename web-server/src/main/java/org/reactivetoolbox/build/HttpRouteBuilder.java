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
import org.reactivetoolbox.eventbus.Handler;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class HttpRouteBuilder {
    private final HttpMethod method;
    private Path path;

    private HttpRouteBuilder(final HttpMethod method) {
        this.method = method;
    }

    public static HttpRouteBuilder routeFor(final HttpMethod method) {
        return new HttpRouteBuilder(method);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R> RouteEnricher<R, RequestContext> withHandler(Handler<R, RequestContext> handler) {
        return RouteEnricher.of(path, handler);
    }

    public HttpRouteBuilder to(final String path) {
        this.path = Path.of(path, method);
        return this;
    }

    public <T1> ParameterBuilder1<T1> withParameters(final Parameter<T1> param1) {
        return ParameterBuilder.of(this, param1);
    }

    public <T1, T2> ParameterBuilder2<T1, T2> withParameters(final Parameter<T1> param1,
                                                             final Parameter<T2> param2) {
        return ParameterBuilder.of(this, param1, param2);
    }

    public <T1, T2, T3> ParameterBuilder3<T1, T2, T3> withParameters(final Parameter<T1> param1,
                                                                     final Parameter<T2> param2,
                                                                     final Parameter<T3> param3) {
        return ParameterBuilder.of(this, param1, param2, param3);
    }

    public <T1, T2, T3, T4> ParameterBuilder4<T1, T2, T3, T4> withParameters(final Parameter<T1> param1,
                                                                             final Parameter<T2> param2,
                                                                             final Parameter<T3> param3,
                                                                             final Parameter<T4> param4) {
        return ParameterBuilder.of(this, param1, param2, param3, param4);
    }

    public <T1, T2, T3, T4, T5> ParameterBuilder5<T1, T2, T3, T4, T5> withParameters(final Parameter<T1> param1,
                                                                                     final Parameter<T2> param2,
                                                                                     final Parameter<T3> param3,
                                                                                     final Parameter<T4> param4,
                                                                                     final Parameter<T5> param5) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5);
    }

    public <T1, T2, T3, T4, T5, T6> ParameterBuilder6<T1, T2, T3, T4, T5, T6> withParameters(final Parameter<T1> param1,
                                                                                             final Parameter<T2> param2,
                                                                                             final Parameter<T3> param3,
                                                                                             final Parameter<T4> param4,
                                                                                             final Parameter<T5> param5,
                                                                                             final Parameter<T6> param6) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> withParameters(
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7) {
        return ParameterBuilder.of(this, param1, param2, param3, param4, param5, param6, param7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> withParameters(
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

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> withParameters(
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
}
