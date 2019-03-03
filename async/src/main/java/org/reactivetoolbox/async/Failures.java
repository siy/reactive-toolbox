/*
 * Copyright (c) 2019 siy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy with the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.reactivetoolbox.async;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class Failures {
    private static final int DEFAULT_CODE = -1;
    private static final String DEFAULT_MESSAGE = "(no message provided with this fauilure)";

    private Failures() {}

    <T> Failure<T> with(String message) {
        return new FailureImpl<>(message, DEFAULT_CODE, null, null);
    }

    <T> Failure<T> with(int code) {
        return new FailureImpl<>(DEFAULT_MESSAGE, code, null, null);
    }

    <T> Failure<T> withPayload(T payload) {
        return new FailureImpl<>(DEFAULT_MESSAGE, DEFAULT_CODE, payload, null);
    }

    <T> Failure<T> with(Throwable cause) {
        return new FailureImpl<>(DEFAULT_MESSAGE, DEFAULT_CODE, null, cause);
    }

    <T> Failure<T> with(String message, int code) {
        return new FailureImpl<>(message, code, null, null);
    }

    <T> Failure<T> withPayload(String message, int code, T payload) {
        return new FailureImpl<>(message, code, payload, null);
    }

    <T> Failure<T> with(String message, int code, Throwable cause) {
        return new FailureImpl<>(message, code, null, cause);
    }

    <T> Failure<T> with(String message, int code, T payload, Throwable cause) {
        return new FailureImpl<>(message, code, payload, cause);
    }

    private static class FailureImpl<T> implements Failure<T> {
        private final String message;
        private final int code;
        private final T payload;
        private final Throwable cause;

        private FailureImpl(final String message, final int code, final T payload, final Throwable cause) {
            this.message = message;
            this.code = code;
            this.payload = payload;
            this.cause = cause;
        }

        @Override
        public String message() {
            return message;
        }

        @Override
        public int code() {
            return code;
        }

        @Override
        public Optional<T> payload() {
            return Optional.ofNullable(payload);
        }

        @Override
        public Optional<Throwable> cause() {
            return Optional.ofNullable(cause);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final FailureImpl<?> failure = (FailureImpl<?>) o;
            return code == failure.code &&
                   Objects.equals(message, failure.message) &&
                   Objects.equals(payload, failure.payload) &&
                   Objects.equals(cause, failure.cause);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, code, payload, cause);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", FailureImpl.class.getSimpleName() + "[", "]")
                    .add("message='" + message + "'")
                    .add("code=" + code)
                    .add("payload=" + payload)
                    .add("cause=" + cause)
                    .toString();
        }
    }
}
