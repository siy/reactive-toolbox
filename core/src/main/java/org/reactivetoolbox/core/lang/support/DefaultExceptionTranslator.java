package org.reactivetoolbox.core.lang.support;

/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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
import org.reactivetoolbox.core.lang.functional.FailureType;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.NoSuchElementException;

/**
 * Default conversion from exception to {@link Failure}.
 * The strategy for conversion is very simple: all errors and some well known cases (like NPE) are considered "internal server error",
 * remaining converted into "bad request". <br/>
 * For custom conversion strategies it might be convenient to filter out some specific exceptions and for remaining ones delegate work to
 * default conversion.
 */
public class DefaultExceptionTranslator {
    public static Failure translate(final Throwable throwable) {
        return Failure.failure(convertType(throwable), "%s\n%s",
                               throwable.toString(),
                               formatStackTrace(throwable));
    }

    private static String formatStackTrace(final Throwable throwable) {
        final var builder = new StringBuilder();
        for (final var element : throwable.getStackTrace()) {
            builder.append("at ").append(element.toString()).append("\n");
        }
        return builder.toString();
    }

    private static FailureType convertType(final Throwable throwable) {
        if (throwable instanceof Error ||
            throwable instanceof IOException ||
            throwable instanceof NullPointerException ||
            throwable instanceof NegativeArraySizeException ||
            throwable instanceof IndexOutOfBoundsException ||
            throwable instanceof ClassCastException ||
            throwable instanceof NoSuchElementException ||
            throwable instanceof ReflectiveOperationException ||
            throwable instanceof CertificateException) {
            return WebFailureTypes.INTERNAL_SERVER_ERROR;
        }

        return WebFailureTypes.BAD_REQUEST;
    }
}
