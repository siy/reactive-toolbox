package org.reactivetoolbox.core;
/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.support.WebFailureTypes;

import java.lang.reflect.Type;

/**
 * Common errors which can be returned from various core API's.
 */
public interface Errors {
    // Scheduler
    Failure TIMEOUT = Failure.failure(WebFailureTypes.REQUEST_TIMEOUT, "Processing timeout error");
    Failure CANCELLED = Failure.failure(WebFailureTypes.NO_RESPONSE, "Request cancelled");

    //TypeToken
    static Failure TYPE_ERROR(final Type type) {
        return Failure.failure(WebFailureTypes.INTERNAL_SERVER_ERROR, "Unable to recognize type {0}", type);
    }

    //Various validations - KSUID and others
    static Failure NOT_VALID(final String message) {
        return Failure.failure(WebFailureTypes.UNPROCESSABLE_ENTITY, message);
    }

    static Failure NOT_VALID(final String format, final Object... params) {
        return Failure.failure(WebFailureTypes.UNPROCESSABLE_ENTITY, format, params);
    }
}
