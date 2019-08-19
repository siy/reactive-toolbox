package org.reactivetoolbox.web.server.auth;

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

/**
 * Common authentication/authorization validation errors
 */
public enum AuthError implements BaseError {
    USER_NOT_LOGGED_IN(401, "User is not authenticated"),
    USER_HAS_NOT_ENOUGH_PRIVILEGES(403, "User is not authorized to perform request"),
    ;

    private final int code;
    private final String message;

    AuthError(final int code, final String message) {
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
    public <T> Either<AuthError, T> asFailure() {
        return Either.failure(this);
    }
}