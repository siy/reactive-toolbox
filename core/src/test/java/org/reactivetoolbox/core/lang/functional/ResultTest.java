/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.core.lang.functional;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.support.WebFailureType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.core.lang.support.WebFailureType.INTERNAL_SERVER_ERROR;

class ResultTest {
    static final Failure TEST_FAILURE = Failure.failure(INTERNAL_SERVER_ERROR, "Test error");

    @Test
    void equalsFollowsContract() {
        assertEquals(ok("1"), ok("1"));
        assertNotEquals(ok(1), ok(2));
        assertNotEquals(ok(1), Result.fail(Errors.TIMEOUT));
    }

    @Test
    void orSelectsFirstSuccess() {
        assertEquals(ok(1), ok(1).or(ok(2)).or(ok(3)));
        assertEquals(ok(2), Result.fail(TEST_FAILURE).or(ok(2)).or(ok(3)));
        assertEquals(ok(3), Result.fail(TEST_FAILURE).or(Result.fail(TEST_FAILURE)).or(ok(3)));
    }

    @Test
    void ifSuccessCalledForSuccessResult() {
        final Integer[] result = new Integer[1];
        ok(5).onSuccess(v -> result[0] = v);
        assertEquals(5, result[0]);
    }

    @Test
    void ifFailureCalledForSuccessResult() {
        final Failure[] result = new Failure[1];
        Result.fail(TEST_FAILURE).onFailure(v -> result[0] = v);
        assertEquals(TEST_FAILURE, result[0]);
    }

    @Test
    void rangeValidatorTest() {
        validateBetween(19, 20, 100)
                .map(Objects::toString)
                .onSuccess(val -> fail());
        validateBetween(101, 20, 100)
                .map(Objects::toString)
                .onSuccess(val -> fail());
        validateBetween(20, 20, 100)
                .map(Objects::toString)
                .onFailure(val -> fail());
        validateBetween(100, 20, 100)
                .map(Objects::toString)
                .onFailure(val -> fail());
        validateBetween(60, 20, 100)
                .map(Objects::toString)
                .onFailure(val -> fail());
    }

    private Result<Integer> validateBetween(final int value, final int min, final int max) {
        return validateGE(value, min).flatMap(val -> validateLE(val, max));
    }

    private Result<Integer> validateGE(final int value, final int min) {
        return value < min ? Result.fail(Failure.failure(WebFailureType.UNPROCESSABLE_ENTITY, "Input value below %d", min))
                           : ok(value);
    }

    private Result<Integer> validateLE(final int value, final int max) {
        return value > max ? Result.fail(Failure.failure(WebFailureType.UNPROCESSABLE_ENTITY, "Input value above %d", max))
                           : ok(value);
    }
}