package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExperimentTest {
    private Service service;

    @Test
    @Disabled
    void declareHandlerTest() {
        final Server server = new Server();

        server.on()
              .get("/some/path/{one}").with(Parameters.inPath(String.class, "one").validated(Validations::notNull),
                                            Parameters.inQuery(Integer.class, "two").validated(Validations::range, 1, 10),
                                            Parameters.inBody(UUID.class).validated(Validations::notNull))
              .perform(input -> input.map((s, i, u) -> service.invoke(s, i, u)));
    }

    private static class Validations {
        public static <T> Either<ErrorDescriptor, T> notNull(final T input) {
            //TODO: what to do with error type here?
            return input == null ? Either.failure(ErrorDescriptor.PARAMETER_IS_NULL) : Either.success(input);
        }

        public static <T extends Number> Either<ErrorDescriptor, T> range(final T input, final int min, final int max) {
            if (input.intValue() < min) {
                return Either.failure(ErrorDescriptor.PARAMETER_IS_BELOW_RANGE);
            }
            if (input.intValue() > max) {
                return Either.failure(ErrorDescriptor.PARAMETER_IS_ABOVE_RANGE);
            }
            return Either.success(input);
        }
    }

    private static class Server {
        public <E> HandlerBuilder on() {
            return null;
        }
    }

    private static class Service {
        public Promise<ErrorDescriptor, String> invoke(final String s, final Integer i, final UUID u) {
            return Promises.success("Hello world!");
        }
    }

    private static class ErrorDescriptor {
        public static final ErrorDescriptor PARAMETER_IS_NULL = new ErrorDescriptor();
        public static final ErrorDescriptor PARAMETER_IS_BELOW_RANGE = new ErrorDescriptor();
        public static final ErrorDescriptor PARAMETER_IS_ABOVE_RANGE = new ErrorDescriptor();
    }

    public static class HandlerBuilder {
        public HandlerBuilder get(final String requestPath) {
            return this;
        }

        public <T> Performer<Tuple1<T>> with(final Parameters.Parameter<T> first) {
            return new Performer<>();
        }
        public <T1, T2> Performer<Tuple2<T1, T2>> with(final Parameters.Parameter<T1> first,
                                                       final Parameters.Parameter<T2> second) {
            return new Performer<>();
        }
        public <T1, T2, T3> Performer<Tuple3<T1, T2, T3>> with(final Parameters.Parameter<T1> first,
                                                               final Parameters.Parameter<T2> second,
                                                               final Parameters.Parameter<T3> third) {
            return new Performer<>();
        }
        public <T1, T2, T3, T4> Performer<Tuple4<T1, T2, T3, T4>> with(final Parameters.Parameter<T1> first,
                                                                       final Parameters.Parameter<T2> second,
                                                                       final Parameters.Parameter<T3> third,
                                                                       final Parameters.Parameter<T4> fourth) {
            return new Performer<>();
        }
    }

    public static class Parameters {
        public static <T> Parameter<T> inPath(final Class<T> type, final String name) {
            return new Parameter<>();
        }

        public static <T> Parameter<T> inQuery(final Class<T> type, final String name) {
            return new Parameter<>();
        }

        public static <T> Parameter<T> inBody(final Class<T> type) {
            return new Parameter<>();
        }

        //TODO: expand it to per-class, like Context, Request, Response, etc.
        public static <T> Parameter<T> internal(final Class<T> type) {
            return new Parameter<>();
        }

        //TODO: add convert(request) -> Either<E, T>
        public static class Parameter<T> {
            private final List<FN1<Either<?, T>, T>> validators = new ArrayList<>();

            public Parameter<T> validated(final FN1<Either<?, T>, T> validator) {
                validators.add(validator);
                return this;
            }

            public <T1> Parameter<T> validated(final FN2<Either<?, T>, T, T1> validator, final T1 param1) {
                validators.add((input) -> validator.apply(input, param1));
                return this;
            }

            public <T1, T2> Parameter<T> validated(final FN3<Either<?, T>, T, T1, T2> validator, final T1 param1, final T2 param2) {
                validators.add((input) -> validator.apply(input, param1, param2));
                return this;
            }
        }
    }

    private static class Performer<T> {
        public void perform(final FN1<Promise<?,?>, T> handler) {
        }
    }
}
