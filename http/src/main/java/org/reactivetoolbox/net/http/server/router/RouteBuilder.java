package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.core.lang.functional.Functions.FN0;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Functions.FN3;
import org.reactivetoolbox.core.lang.functional.Functions.FN4;
import org.reactivetoolbox.core.lang.functional.Functions.FN5;
import org.reactivetoolbox.core.lang.functional.Functions.FN6;
import org.reactivetoolbox.core.lang.functional.Functions.FN7;
import org.reactivetoolbox.core.lang.functional.Functions.FN8;
import org.reactivetoolbox.core.lang.functional.Functions.FN9;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.Tuple;
import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.Tuple.Tuple4;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.net.http.ContentType;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.NativeBuffer;
import org.reactivetoolbox.net.http.server.RequestContext;

import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.net.http.Method.CONNECT;
import static org.reactivetoolbox.net.http.Method.DELETE;
import static org.reactivetoolbox.net.http.Method.GET;
import static org.reactivetoolbox.net.http.Method.HEAD;
import static org.reactivetoolbox.net.http.Method.OPTIONS;
import static org.reactivetoolbox.net.http.Method.PATCH;
import static org.reactivetoolbox.net.http.Method.POST;
import static org.reactivetoolbox.net.http.Method.PUT;
import static org.reactivetoolbox.net.http.Method.TRACE;

public final class RouteBuilder {
    private RouteBuilder() {
    }

    static Stage1 options() {
        return new Builder(OPTIONS);
    }

    static Stage1 options(final String path) {
        return new Builder(OPTIONS, path);
    }

    static Stage1 get() {
        return new Builder(GET);
    }

    static Stage1 get(final String path) {
        return new Builder(GET, path);
    }

    static Stage1 head() {
        return new Builder(HEAD);
    }

    static Stage1 head(final String path) {
        return new Builder(HEAD, path);
    }

    static Stage1 post() {
        return new Builder(POST);
    }

    static Stage1 post(final String path) {
        return new Builder(POST, path);
    }

    static Stage1 put() {
        return new Builder(PUT);
    }

    static Stage1 put(final String path) {
        return new Builder(PUT, path);
    }

    static Stage1 patch() {
        return new Builder(PATCH);
    }

    static Stage1 patch(final String path) {
        return new Builder(PATCH, path);
    }

    static Stage1 delete() {
        return new Builder(DELETE);
    }

    static Stage1 delete(final String path) {
        return new Builder(DELETE, path);
    }

    static Stage1 trace() {
        return new Builder(TRACE);
    }

    static Stage1 trace(final String path) {
        return new Builder(TRACE, path);
    }

    static Stage1 connect() {
        return new Builder(CONNECT);
    }

    static Stage1 connect(final String path) {
        return new Builder(CONNECT, path);
    }

    public interface Stage1 {
        Stage1 accepts(final ContentType input);

        Stage1 returns(final ContentType output);

        Stage2_0 without();

        <T1> Stage2_1<T1> with(final RequestParameter<T1> p1);

        <T1, T2> Stage2_2<T1, T2> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2);

        <T1, T2, T3> Stage2_3<T1, T2, T3> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2, final RequestParameter<T3> p3);

        <T1, T2, T3, T4> Stage2_4<T1, T2, T3, T4> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2,
                                                       final RequestParameter<T3> p3, final RequestParameter<T4> p4);

        <T1, T2, T3, T4, T5> Stage2_5<T1, T2, T3, T4, T5> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2, final RequestParameter<T3> p3,
                                                               final RequestParameter<T4> p4, final RequestParameter<T5> p5);

        <T1, T2, T3, T4, T5, T6> Stage2_6<T1, T2, T3, T4, T5, T6> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2, final RequestParameter<T3> p3,
                                                                       final RequestParameter<T4> p4, final RequestParameter<T5> p5, final RequestParameter<T6> p6);

        <T1, T2, T3, T4, T5, T6, T7> Stage2_7<T1, T2, T3, T4, T5, T6, T7> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2, final RequestParameter<T3> p3,
                                                                               final RequestParameter<T4> p4, final RequestParameter<T5> p5, final RequestParameter<T6> p6,
                                                                               final RequestParameter<T7> p7);

        <T1, T2, T3, T4, T5, T6, T7, T8> Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2,
                                                                                       final RequestParameter<T3> p3, final RequestParameter<T4> p4,
                                                                                       final RequestParameter<T5> p5, final RequestParameter<T6> p6,
                                                                                       final RequestParameter<T7> p7, final RequestParameter<T8> p8);

        <T1, T2, T3, T4, T5, T6, T7, T8, T9> Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2,
                                                                                               final RequestParameter<T3> p3, final RequestParameter<T4> p4,
                                                                                               final RequestParameter<T5> p5, final RequestParameter<T6> p6,
                                                                                               final RequestParameter<T7> p7, final RequestParameter<T8> p8,
                                                                                               final RequestParameter<T9> p9);
    }

    public interface Stage2_0 {
        <R> Route then(FN0<Promise<R>> fn);
    }

    public interface Stage2_1<T1> {
        Stage2_1<T1> validate(final CrossParameterValidator<Tuple1<T1>> validator);

        <R> Route then(final FN1<Promise<R>, T1> fn);
    }

    public interface Stage2_2<T1, T2> {
        Stage2_2<T1, T2> validate(final CrossParameterValidator<Tuple2<T1, T2>> validator);

        <R> Route then(FN2<Promise<R>, T1, T2> fn);
    }

    public interface Stage2_3<T1, T2, T3> {
        Stage2_3<T1, T2, T3> validate(final CrossParameterValidator<Tuple3<T1, T2, T3>> validator);

        <R> Route then(FN3<Promise<R>, T1, T2, T3> fn);
    }

    public interface Stage2_4<T1, T2, T3, T4> {
        Stage2_4<T1, T2, T3, T4> validate(final CrossParameterValidator<Tuple4<T1, T2, T3, T4>> validator);

        <R> Route then(FN4<Promise<R>, T1, T2, T3, T4> fn);
    }

    public interface Stage2_5<T1, T2, T3, T4, T5> {
        Stage2_5<T1, T2, T3, T4, T5> validate(final CrossParameterValidator<Tuple5<T1, T2, T3, T4, T5>> validator);

        <R> Route then(FN5<Promise<R>, T1, T2, T3, T4, T5> fn);
    }

    public interface Stage2_6<T1, T2, T3, T4, T5, T6> {
        Stage2_6<T1, T2, T3, T4, T5, T6> validate(final CrossParameterValidator<Tuple6<T1, T2, T3, T4, T5, T6>> validator);

        <R> Route then(FN6<Promise<R>, T1, T2, T3, T4, T5, T6> fn);
    }

    public interface Stage2_7<T1, T2, T3, T4, T5, T6, T7> {
        Stage2_7<T1, T2, T3, T4, T5, T6, T7> validate(final CrossParameterValidator<Tuple7<T1, T2, T3, T4, T5, T6, T7>> validator);

        <R> Route then(FN7<Promise<R>, T1, T2, T3, T4, T5, T6, T7> fn);
    }

    public interface Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8> {
        Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8> validate(final CrossParameterValidator<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> validator);

        <R> Route then(FN8<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8> fn);
    }

    public interface Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validate(final CrossParameterValidator<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> validator);

        <R> Route then(FN9<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> fn);
    }

    private static class Builder implements Stage1, Prefixed<Route> {
        private final Method method;
        private String path;
        private ContentType input = ContentType.JSON;
        private ContentType output = ContentType.JSON;
        private FN1<Promise<?>, RequestContext> handler;

        private Builder(final Method method) {
            this(method, "");
        }

        private Builder(final Method method, final String path) {
            this.method = method;
            this.path = path;
        }

        @Override
        public Stage1 accepts(final ContentType input) {
            this.input = input;
            return this;
        }

        @Override
        public Stage1 returns(final ContentType output) {
            this.output = output;
            return this;
        }

        @Override
        public Route prefix(final String prefix) {
            return route().prefix(prefix);
        }

        private Route route() {
            //TODO: finish it
            final FN1<Promise<NativeBuffer>, RequestContext> fullHandler = null;

            return Route.route(Path.path(method, path), fullHandler, input, output);
        }

        private <T extends Tuple, R> Route setHandler(final FN1<Result<T>, RequestContext> extractor,
                                                      CrossParameterValidator<T> validator,
                                                      FN1<Promise<R>, T> handler) {
            this.handler = extractor.then(result -> result.flatMap(validator).fold(Promise::readyFail, handler));
            return route();
        }

        @Override
        public Stage2_0 without() {
            return new Stage2_0() {
                @Override
                public <R> Route then(FN0<Promise<R>> fn) {
                    return setHandler(context -> ok(tuple()),
                                      Result::ok,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1> Stage2_1<T1> with(final RequestParameter<T1> p1) {
            return new Stage2_1<T1>() {
                private CrossParameterValidator<Tuple1<T1>> validator = Result::ok;

                @Override
                public Stage2_1<T1> validate(final CrossParameterValidator<Tuple1<T1>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(final FN1<Promise<R>, T1> fn) {
                    return setHandler(context -> tuple(p1.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2> Stage2_2<T1, T2> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2) {
            return new Stage2_2<T1, T2>() {
                private CrossParameterValidator<Tuple2<T1, T2>> validator = Result::ok;

                @Override
                public Stage2_2<T1, T2> validate(final CrossParameterValidator<Tuple2<T1, T2>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN2<Promise<R>, T1, T2> fn) {
                    return setHandler(context -> tuple(p1.apply(context),
                                                       p2.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3> Stage2_3<T1, T2, T3> with(final RequestParameter<T1> p1, final RequestParameter<T2> p2, final RequestParameter<T3> p3) {
            return new Stage2_3<T1, T2, T3>() {
                private CrossParameterValidator<Tuple3<T1, T2, T3>> validator = Result::ok;

                @Override
                public Stage2_3<T1, T2, T3> validate(final CrossParameterValidator<Tuple3<T1, T2, T3>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN3<Promise<R>, T1, T2, T3> fn) {
                    return setHandler(context -> tuple(p1.apply(context),
                                                       p2.apply(context),
                                                       p3.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4> Stage2_4<T1, T2, T3, T4> with(final RequestParameter<T1> p1,
                                                              final RequestParameter<T2> p2,
                                                              final RequestParameter<T3> p3,
                                                              final RequestParameter<T4> p4) {
            return new Stage2_4<T1, T2, T3, T4>() {
                private CrossParameterValidator<Tuple4<T1, T2, T3, T4>> validator = Result::ok;

                @Override
                public Stage2_4<T1, T2, T3, T4> validate(final CrossParameterValidator<Tuple4<T1, T2, T3, T4>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN4<Promise<R>, T1, T2, T3, T4> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context),
                                                       p3.apply(context), p4.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4, T5> Stage2_5<T1, T2, T3, T4, T5> with(final RequestParameter<T1> p1,
                                                                      final RequestParameter<T2> p2,
                                                                      final RequestParameter<T3> p3,
                                                                      final RequestParameter<T4> p4,
                                                                      final RequestParameter<T5> p5) {
            return new Stage2_5<T1, T2, T3, T4, T5>() {
                private CrossParameterValidator<Tuple5<T1, T2, T3, T4, T5>> validator = Result::ok;

                @Override
                public Stage2_5<T1, T2, T3, T4, T5> validate(final CrossParameterValidator<Tuple5<T1, T2, T3, T4, T5>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN5<Promise<R>, T1, T2, T3, T4, T5> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context), p3.apply(context),
                                                       p4.apply(context), p5.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4, T5, T6> Stage2_6<T1, T2, T3, T4, T5, T6> with(final RequestParameter<T1> p1,
                                                                              final RequestParameter<T2> p2,
                                                                              final RequestParameter<T3> p3,
                                                                              final RequestParameter<T4> p4,
                                                                              final RequestParameter<T5> p5,
                                                                              final RequestParameter<T6> p6) {
            return new Stage2_6<T1, T2, T3, T4, T5, T6>() {
                private CrossParameterValidator<Tuple6<T1, T2, T3, T4, T5, T6>> validator = Result::ok;

                @Override
                public Stage2_6<T1, T2, T3, T4, T5, T6> validate(final CrossParameterValidator<Tuple6<T1, T2, T3, T4, T5, T6>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN6<Promise<R>, T1, T2, T3, T4, T5, T6> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context), p3.apply(context), p4.apply(context),
                                                       p5.apply(context), p6.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4, T5, T6, T7> Stage2_7<T1, T2, T3, T4, T5, T6, T7> with(final RequestParameter<T1> p1,
                                                                                      final RequestParameter<T2> p2,
                                                                                      final RequestParameter<T3> p3,
                                                                                      final RequestParameter<T4> p4,
                                                                                      final RequestParameter<T5> p5,
                                                                                      final RequestParameter<T6> p6,
                                                                                      final RequestParameter<T7> p7) {
            return new Stage2_7<T1, T2, T3, T4, T5, T6, T7>() {
                private CrossParameterValidator<Tuple7<T1, T2, T3, T4, T5, T6, T7>> validator = Result::ok;

                @Override
                public Stage2_7<T1, T2, T3, T4, T5, T6, T7> validate(final CrossParameterValidator<Tuple7<T1, T2, T3, T4, T5, T6, T7>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN7<Promise<R>, T1, T2, T3, T4, T5, T6, T7> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context), p3.apply(context), p4.apply(context),
                                                       p5.apply(context), p6.apply(context), p7.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4, T5, T6, T7, T8> Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8> with(final RequestParameter<T1> p1,
                                                                                              final RequestParameter<T2> p2,
                                                                                              final RequestParameter<T3> p3,
                                                                                              final RequestParameter<T4> p4,
                                                                                              final RequestParameter<T5> p5,
                                                                                              final RequestParameter<T6> p6,
                                                                                              final RequestParameter<T7> p7,
                                                                                              final RequestParameter<T8> p8) {
            return new Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8>() {
                private CrossParameterValidator<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> validator = Result::ok;

                @Override
                public Stage2_8<T1, T2, T3, T4, T5, T6, T7, T8> validate(final CrossParameterValidator<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN8<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context), p3.apply(context), p4.apply(context),
                                                       p5.apply(context), p6.apply(context), p7.apply(context), p8.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }

        @Override
        public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(final RequestParameter<T1> p1,
                                                                                                      final RequestParameter<T2> p2,
                                                                                                      final RequestParameter<T3> p3,
                                                                                                      final RequestParameter<T4> p4,
                                                                                                      final RequestParameter<T5> p5,
                                                                                                      final RequestParameter<T6> p6,
                                                                                                      final RequestParameter<T7> p7,
                                                                                                      final RequestParameter<T8> p8,
                                                                                                      final RequestParameter<T9> p9) {
            return new Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9>() {
                private CrossParameterValidator<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> validator = Result::ok;

                @Override
                public Stage2_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validate(final CrossParameterValidator<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> validator) {
                    this.validator = validator;
                    return this;
                }

                @Override
                public <R> Route then(FN9<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> fn) {
                    return setHandler(context -> tuple(p1.apply(context), p2.apply(context), p3.apply(context), p4.apply(context), p5.apply(context),
                                                       p6.apply(context), p7.apply(context), p8.apply(context), p9.apply(context)).map(Result::flatten),
                                      validator,
                                      fn.bindTuple());
                }
            };
        }
    }
}
