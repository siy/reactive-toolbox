package org.reactivetoolbox.value.conversion.var;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.value.conversion.Converter;
import org.reactivetoolbox.value.validation.Validator;

/**
 * Container for parameter definition.
 *
 * @param <T>
 *        Type of the parameter value
 */
public class Var<T> {
    private final Converter<T> converter;
    private final Option<VarDescription> parameterDescription;

    private Var(final Converter<T> converter, final String name) {
        this(converter, Option.of(VarDescription.of(name, converter.typeDescription(), null)));
    }

    private Var(final Converter<T> converter, final Option<VarDescription> parameterDescription) {
        this.converter = converter;
        this.parameterDescription = parameterDescription;
    }

    public static <T> Var<T> of(final Converter<T> converter, final String name) {
        return new Var<>(converter, name);
    }

    public static <T> Var<T> of(final Converter<T> converter, final Option<VarDescription> parameterDescription) {
        return new Var<>(converter, parameterDescription);
    }

    public Converter<T> converter() {
        return converter;
    }

    public Var<T> description(final String description) {
        return new Var<>(converter, parameterDescription.map(value -> value.description(description)));
    }

    public Option<VarDescription> description() {
        return parameterDescription;
    }

    /**
     * Add validation to the parameter.
     *
     * @param validator
     *        The validator to be applied to parameter during conversion of parameter value
     * @param <R>
     *        New type of the parameter value
     * @return updated parameter definition
     */
    public <R> Var<R> and(final Validator<R, T> validator) {
        return validator.modify(this);
    }

    public <R, T1> Var<R> and(final Functions.FN2<Either<? extends BaseError, R>, T, T1> validator, final T1 param1) {
        return and(input -> validator.apply(input, param1));
    }

    public <R, T1, T2> Var<R> and(final Functions.FN3<Either<? extends BaseError, R>, T, T1, T2> validator, final T1 param1, final T2 param2) {
        return and(input -> validator.apply(input, param1, param2));
    }

    public <R, T1, T2, T3> Var<R> and(final Functions.FN4<Either<? extends BaseError, R>, T, T1, T2, T3> validator, final T1 param1, final T2 param2, final T3 param3) {
        return and(input -> validator.apply(input, param1, param2, param3));
    }

    public <R, T1, T2, T3, T4> Var<R> and(final Functions.FN5<Either<? extends BaseError, R>, T, T1, T2, T3, T4> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
        return and(input -> validator.apply(input, param1, param2, param3, param4));
    }

    public <R, T1, T2, T3, T4, T5> Var<R> and(final Functions.FN6<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
        return and(input -> validator.apply(input, param1, param2, param3, param4, param5));
    }

    public <R, T1, T2, T3, T4, T5, T6> Var<R> and(final Functions.FN7<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
        return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6));
    }

    public <R, T1, T2, T3, T4, T5, T6, T7> Var<R> and(final Functions.FN8<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
        return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7));
    }

    public <R, T1, T2, T3, T4, T5, T6, T7, T8> Var<R> and(final Functions.FN9<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
        return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7, param8));
    }
}
