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

//TODO: Javadoc, tests
public interface Is {
    Pattern PASSWORD_CHECKER = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{2,}$");

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

    static Either<? extends BaseError, String> email(final String email) {
        //TODO: implement e-mail validation, implement validator object with description
        return Either.success(email);
    }

    static <T extends Number> Validator<T, T> between(final int min, final int max) {
        return new NumberIntegerRangeValidator<>(min, max);
    }

    static <T extends Number> Validator<T, T> between(final long min, final long max) {
        return new NumberLongRangeValidator<>(min, max);
    }

    static <T extends Number> Validator<T, T> between(final double min, final double max) {
        return new NumberDoubleRangeValidator<>(min, max);
    }

    static <T> Either<ValidationError, T> notNull(final Option<T> input) {
        return input.map(Either::<ValidationError, T>success)
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    static Either<ValidationError, String> notNullOrEmpty(final Option<String> input) {
        return input.map(val -> val.isBlank()
                ? Either.<ValidationError, String>failure(ValidationError.STRING_IS_EMPTY)
                : Either.<ValidationError, String>success(val))
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    static <T extends Number> Either<? extends BaseError, T> between(final T input, final int min, final int max) {
        return new NumberIntegerRangeValidator<T>(min, max).apply(input);
    }

    static <T extends Number> Either<? extends BaseError, T> between(final T input, final long min, final long max) {
        return new NumberLongRangeValidator<T>(min, max).apply(input);
    }

    static <T extends Number> Either<? extends BaseError, T> between(final T input, final double min, final double max) {
        return new NumberDoubleRangeValidator<T>(min, max).apply(input);
    }

    static Either<ValidationError, String> lenBetween(final Option<String> input1, final int minLen, final int maxLen) {
        return notNull(input1)
                .flatMap(input -> input.length() < minLen
                        ? failure(ValidationError.STRING_TOO_SHORT)
                        : input.length() < maxLen
                                ? failure(ValidationError.STRING_TOO_LONG)
                                : Either.success(input));
    }

    static Either<? extends BaseError, Authentication> loggedIn(final Option<Authentication> authentication) {
        return authentication.map(Authentication::token)
                .otherwise(() -> failure(ValidationError.USER_NOT_LOGGED_IN));
    }

    static Either<? extends BaseError, String> strongPassword(final String string) {
        return PASSWORD_CHECKER.matcher(string).find() ? Either.success(string) : Either.failure(ValidationError.WEAK_PASSWORD);
    }

    static Either<? extends BaseError, Authentication> belongsToAll(final Authentication authentication, final Role... roles) {
        return authentication.hasAllRoles(roles);
    }

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
