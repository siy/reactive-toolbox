package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

import static org.junit.jupiter.api.Assertions.*;


class PromisesTest {
    @Test
    void multipleAssignmentsAreIgnored() {
        final Promise<Throwable, Integer> promise = Promises.waiting();

        promise.resolve(Either.right(1));
        promise.resolve(Either.right(2));
        promise.resolve(Either.right(3));
        promise.resolve(Either.right(4));

        assertTrue(promise.value().isPresent());
        assertTrue(promise.value().get().isRight());
        assertEquals(Integer.valueOf(1), promise.value().get().right().get());
    }
    //TODO: finish tests
}
