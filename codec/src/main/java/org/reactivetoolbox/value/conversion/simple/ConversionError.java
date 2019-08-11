package org.reactivetoolbox.value.conversion.simple;

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


//TODO: JavaDoc
public enum ConversionError implements BaseError {
    NOT_AN_INTEGER_NUMBER(422, "Value is not an integer number"),
    NOT_A_LONG_INTEGER_NUMBER(422, "Value is not a long integer number"),
    NOT_A_DOUBLE_NUMBER(422, "Value is not a valid double precision floating point number"),
    NOT_A_VALID_UUID(422, "Value is not a valid UUID"),
    NOT_A_BOOLEAN(422, "Value is not a valid boolean"),
    NOT_A_NUMBER(422, "Value is not a number");

    private final int code;
    private final String message;

    ConversionError(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public <T> Either<ConversionError, T> asFailure() {
        return Either.failure(this);
    }
}
