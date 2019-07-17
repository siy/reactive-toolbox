package org.reactivetoolbox.build;

import org.reactivetoolbox.build.ParameterBuilder.ParameterBuilder0;
import org.reactivetoolbox.build.ParameterBuilder.PB1;
import org.reactivetoolbox.build.ParameterBuilder.PB2;
import org.reactivetoolbox.build.ParameterBuilder.PB3;
import org.reactivetoolbox.build.ParameterBuilder.PB4;
import org.reactivetoolbox.build.ParameterBuilder.PB5;
import org.reactivetoolbox.build.ParameterBuilder.PB6;
import org.reactivetoolbox.build.ParameterBuilder.PB7;
import org.reactivetoolbox.build.ParameterBuilder.PB8;
import org.reactivetoolbox.build.ParameterBuilder.PB9;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.parameter.Parameters.P;

public class HttpRouteBuilder {
    private final Path path;
    private String description = "(no description)";

    private HttpRouteBuilder(final HttpMethod method, final String path) {
        this.path = Path.of(path, method);
    }

    public static HttpRouteBuilder create(final HttpMethod method, final String path) {
        return new HttpRouteBuilder(method, path);
    }

    public <T1> PB1<T1> with(final P<T1> param1) {
        return ParameterBuilder.of(path, description, param1);
    }

    public <T1, T2> PB2<T1, T2> with(final P<T1> param1,
                                     final P<T2> param2) {
        return ParameterBuilder.of(path, description, param1, param2);
    }

    public <T1, T2, T3> PB3<T1, T2, T3> with(final P<T1> param1,
                                             final P<T2> param2,
                                             final P<T3> param3) {
        return ParameterBuilder.of(path, description, param1, param2, param3);
    }

    public <T1, T2, T3, T4> PB4<T1, T2, T3, T4> with(final P<T1> param1,
                                                     final P<T2> param2,
                                                     final P<T3> param3,
                                                     final P<T4> param4) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4);
    }

    public <T1, T2, T3, T4, T5> PB5<T1, T2, T3, T4, T5> with(final P<T1> param1,
                                                             final P<T2> param2,
                                                             final P<T3> param3,
                                                             final P<T4> param4,
                                                             final P<T5> param5) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4, param5);
    }

    public <T1, T2, T3, T4, T5, T6> PB6<T1, T2, T3, T4, T5, T6> with(final P<T1> param1,
                                                                     final P<T2> param2,
                                                                     final P<T3> param3,
                                                                     final P<T4> param4,
                                                                     final P<T5> param5,
                                                                     final P<T6> param6) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4, param5, param6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> PB7<T1, T2, T3, T4, T5, T6, T7> with(
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4, param5, param6, param7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> PB8<T1, T2, T3, T4, T5, T6, T7, T8> with(
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7,
            final P<T8> param8) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7,
            final P<T8> param8,
            final P<T9> param9) {
        return ParameterBuilder.of(path, description, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public ParameterBuilder0 withoutParameters() {
        return ParameterBuilder.of(path, description);
    }

    public HttpRouteBuilder description(final String description) {
        this.description = description;
        return this;
    }
}
