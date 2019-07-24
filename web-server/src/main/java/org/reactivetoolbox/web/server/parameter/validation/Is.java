package org.reactivetoolbox.web.server.parameter.validation;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters;
import org.reactivetoolbox.web.server.parameter.Parameters.P;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.Role;

import java.util.regex.Pattern;

import static org.reactivetoolbox.core.functional.Either.failure;

/**
 * Most common validators
 */
public interface Is {
    Pattern PASSWORD_CHECKER = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{2,}$");

    /**
     * Create new instance of {@link Validator} which validates that required parameter is present.
     * <br>
     * Unlike {@link #notNull(Option)} which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @param <T>
     *        Parameter type
     * @return Created validator
     */
    static <T> Validator<T, Option<T>> required() {
        return new Validator<>() {
            @Override
            public P<T> modify(final P<Option<T>> input) {
                return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                     input.description()
                                          .map(d -> d.mandatory(true))
                                          .map(d -> d.addValidationComment("Required parameter")));
            }

            @Override
            public Either<? extends BaseError, T> apply(final Option<T> param1) {
                return notNull(param1);
            }
        };
    }

    /**
     * Create new instance of {@link Validator} which validates that required string parameter is present and is not
     * an empty string.
     * <br>
     * Unlike {@link #notNullOrEmpty(Option)} which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @return Created validator
     */
    static Validator<String, Option<String>> notNullOrEmpty() {
        return new Validator<>() {
            @Override
            public P<String> modify(final P<Option<String>> input) {
                return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                     input.description()
                                          .map(d -> d.mandatory(true))
                                          .map(d -> d.addValidationComment("Can't be empty")));
            }

            @Override
            public Either<? extends BaseError, String> apply(final Option<String> param1) {
                return notNullOrEmpty(param1);
            }
        };
    }


    /**
     * Create new instance of {@link Validator} which validates that required string parameter is present and is a
     * string with length between specified limits.
     * <br>
     * Unlike {@link #lenBetween(Option, int, int)} which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @return Created validator
     */
    static Validator<String, Option<String>> lenBetween(final int min, final int max) {
        return new Validator<>() {
            @Override
            public P<String> modify(final P<Option<String>> input) {
                return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                     input.description()
                                          .map(d -> d.mandatory(true))
                                          .map(d -> d.addValidationComment("Must have len between " + min + " and " + max)));
            }

            @Override
            public Either<? extends BaseError, String> apply(final Option<String> param1) {
                return lenBetween(param1, min, max);
            }
        };
    }

    /**
     * Create new instance of {@link Validator} which validates that input string represents a password with following
     * properties:
     * <ul>
     *     <li>Contains at least one lower case letter</li>
     *     <li>Contains at least one upper case letter</li>
     *     <li>Contains at least one digit</li>
     *     <li>Contains at least one special character</li>
     *     <li>Contains at least 2 character</li>
     * </ul>
     * Note: if longer password is required, then this validator can be combined with {@link #lenBetween(int, int)} or
     * {@link #lenBetween(Option, int, int)} to enforce required password length.
     * <br>
     * Unlike {@link #strongPassword(String)} which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @return Created validator
     */
    static Validator<String, String> strongPassword() {
        return new Validator<>() {
            @Override
            public P<String> modify(final P<String> input) {
                return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                     input.description()
                                          .map(d -> d.mandatory(true))
                                          .map(d -> d.addValidationComment("Must contain at least one: upper case letter, "
                                                                           + "lower case letter, digit and special character")));
            }

            @Override
            public Either<? extends BaseError, String> apply(final String param1) {
                return strongPassword(param1);
            }
        };
    }

    //TODO: finish it
    static Validator<String, String> email() {
        return new Validator<>() {
            @Override
            public P<String> modify(final P<String> input) {
                return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                     input.description()
                                          .map(d -> d.mandatory(true))
                                          .map(d -> d.addValidationComment("Must be a correctly formatted e-mail address.")));
            }

            @Override
            public Either<? extends BaseError, String> apply(final String param1) {
                return email(param1);
            }
        };
    }

    //TODO: finish it
    static Either<? extends BaseError, String> email(final String email) {
        return Either.success(email);
    }

    /**
     * Create new instance of {@link Validator} which validates that number input is between specified integer bounds.
     * <br>
     * Unlike {@link #between(Number, int, int)}  which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Created validator
     */
    static <T extends Number> Validator<T, T> between(final int min, final int max) {
        return new NumberIntegerRangeValidator<>(min, max);
    }

    /**
     * Create new instance of {@link Validator} which validates that number input is between specified long integer bounds.
     * <br>
     * Unlike {@link #between(Number, long, long)}  which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Created validator
     */
    static <T extends Number> Validator<T, T> between(final long min, final long max) {
        return new NumberLongRangeValidator<>(min, max);
    }

    /**
     * Create new instance of {@link Validator} which validates that number input is between specified double precision
     * floating point bounds.
     * <br>
     * Unlike {@link #between(Number, double, double)}  which also may serve as such validator, the {@link Validator} instance
     * created with this method adds necessary comments for parameter into parameter description.
     *
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Created validator
     */
    static <T extends Number> Validator<T, T> between(final double min, final double max) {
        return new NumberDoubleRangeValidator<>(min, max);
    }

    /**
     * Validate that parameter is not <code>null</code>. This method serves as a converting {@link Validator} and
     * transforms optional parameter value into actual parameter value or causes a validation failure if
     * parameter is missing
     *
     * @param input
     *        Input parameter
     * @param <T>
     *        Input parameter type
     * @return Validation result
     */
    static <T> Either<ValidationError, T> notNull(final Option<T> input) {
        return input.map(Either::<ValidationError, T>success)
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    /**
     * Validate that string parameter is not <code>null</code> and does not represent an empty string.
     * This method serves as a converting {@link Validator} and transforms optional parameter string into actual string
     * or causes a validation failure if string is empty or missing
     *
     * @param input
     *        Input parameter
     * @return Validation result
     */
    static Either<ValidationError, String> notNullOrEmpty(final Option<String> input) {
        return input.map(val -> val.isBlank()
                ? Either.<ValidationError, String>failure(ValidationError.STRING_IS_EMPTY)
                : Either.<ValidationError, String>success(val))
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    /**
     * Validate that number parameter is between specified integer bounds.
     *
     * @param input
     *        Input parameter
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Validation result
     */
    static <T extends Number> Either<? extends BaseError, T> between(final T input, final int min, final int max) {
        return new NumberIntegerRangeValidator<T>(min, max).apply(input);
    }

    /**
     * Validate that number parameter is between specified long integer bounds.
     *
     * @param input
     *        Input parameter
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Validation result
     */
    static <T extends Number> Either<? extends BaseError, T> between(final T input, final long min, final long max) {
        return new NumberLongRangeValidator<T>(min, max).apply(input);
    }

    /**
     * Validate that number parameter is between specified double precision floating point bounds.
     *
     * @param input
     *        Input parameter
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Validation result
     */
    static <T extends Number> Either<? extends BaseError, T> between(final T input, final double min, final double max) {
        return new NumberDoubleRangeValidator<T>(min, max).apply(input);
    }

    /**
     * Validate that optional string parameter is not null and has length within specified range. If parameter value
     * passes validation, then actual string parameter value is returned.
     *
     * @param input1
     *        Input parameter
     * @param min
     *        Inclusive lower bound
     * @param max
     *        Inclusive upper bound
     * @return Validation result
     */
    static Either<ValidationError, String> lenBetween(final Option<String> input1, final int minLen, final int maxLen) {
        return notNull(input1)
                .flatMap(input -> input.length() < minLen
                        ? failure(ValidationError.STRING_TOO_SHORT)
                        : input.length() > maxLen
                                ? Either.success(input)
                                : failure(ValidationError.STRING_TOO_LONG));
    }

    /**
     * Validate that user is logged in. The validation is applicable to JWT authentication only and checks that
     * JWT token is valid and is not expired. This validation converts optional input value into actual value
     * if parameter is present and valid.
     *
     * @param authentication
     *        Input parameter
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> loggedIn(final Option<Authentication> authentication) {
        return authentication.map(Authentication::token)
                .otherwise(() -> failure(ValidationError.USER_NOT_LOGGED_IN));
    }

    /**
     * Validate that input string represents a password with following properties:
     * <ul>
     * <li>Contains at least one lower case letter</li>
     *        <li>Contains at least one upper case letter</li>
     *        <li>Contains at least one digit</li>
     *        <li>Contains at least one special character</li>
     *        <li>Contains at least 2 character</li>
     * </ul>
     * Note: if longer password is required, then this validator can be combined with {@link #lenBetween(int, int)} or
     * {@link #lenBetween(Option, int, int)} to enforce required password length.
     *
     * @param string
     *        Input parameter
     *
     * @return Validation result
     */
    static Either<? extends BaseError, String> strongPassword(final String string) {
        return PASSWORD_CHECKER.matcher(string).find() ? Either.success(string) : Either.failure(ValidationError.WEAK_PASSWORD);
    }

    /**
     * Check that user belongs to all roles specified as parameters. Note that this implementation is applicable
     * only to {@link Authentication} implementations which support role validation.
     *
     * @param authentication
     *        Input parameter
     * @param roles
     *        Roles to check
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> belongsToAll(final Authentication authentication, final Role... roles) {
        return authentication.hasAllRoles(roles);
    }

    /**
     * Check that user belongs to any roles specified as parameters. Note that this implementation is applicable
     * only to {@link Authentication} implementations which support role validation.
     *
     * @param authentication
     *        Input parameter
     * @param roles
     *        Roles to check
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> belongsToAny(final Authentication authentication, final Role... roles) {
        return authentication.hasAnyRoles(roles);
    }

    abstract class RangeValidator<T extends Number, V extends Comparable> implements Validator<T, T> {
        private final V min;
        private final V max;

        public RangeValidator(final V min, final V max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public P<T> modify(final P<T> input) {
            return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply),
                                 input.description()
                                      .map(d -> d.addValidationComment("Value must be between " + min + " and " + max)));
        }

        abstract V value(final T input);

        @Override
        @SuppressWarnings("unchecked")
        public Either<? extends BaseError, T> apply(final T param1) {
            return value(param1).compareTo(min) < 0
                   ? failure(ValidationError.NUMBER_IS_BELOW_LOWER_BOUND)
                   : value(param1).compareTo(max) > 0
                     ? failure(ValidationError.NUMBER_IS_ABOVE_UPPER_BOUND)
                     : Either.success(param1);
        }
    }

    class NumberIntegerRangeValidator<T extends Number> extends RangeValidator<T, Integer> {
        public NumberIntegerRangeValidator(final int min, final int max) {
            super(min, max);
        }

        @Override
        Integer value(final Number input) {
            return input.intValue();
        }
    }

    class NumberLongRangeValidator<T extends Number> extends RangeValidator<T, Long> {
        public NumberLongRangeValidator(final long min, final long max) {
            super(min, max);
        }

        @Override
        Long value(final Number input) {
            return input.longValue();
        }
    }

    class NumberDoubleRangeValidator<T extends Number> extends RangeValidator<T, Double> {
        public NumberDoubleRangeValidator(final double min, final double max) {
            super(min, max);
        }

        @Override
        Double value(final Number input) {
            return input.doubleValue();
        }
    }
}
