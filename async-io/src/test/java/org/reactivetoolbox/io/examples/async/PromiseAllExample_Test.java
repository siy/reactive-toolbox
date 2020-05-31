package org.reactivetoolbox.io.examples.async;

import org.junit.jupiter.api.Test;

import static org.reactivetoolbox.core.Errors.TIMEOUT;
import static org.reactivetoolbox.io.async.Promise.all;
import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

public class PromiseAllExample_Test {
    private final AsyncService service = new AsyncService();

    @Test
    void simpleAsyncTask() {
        service.slowRetrieveInteger(42)
               .onResult(result -> result.onSuccess(System.out::println))
               .syncWait();
    }

    @Test
    void simpleAsyncTaskWithTimeout() {
        service.slowRetrieveInteger(4242)
               .when(timeout(10).seconds(), fail(TIMEOUT))
               .onResult(result -> result.onSuccess(System.out::println))
               .syncWait();
    }

    @Test
    void waitForAllResults1() {
        all(service.slowRetrieveInteger(123),
            service.slowRetrieveString("text 1"),
            service.slowRetrieveUuid())
                .onResult(result -> result.onSuccess(System.out::println))
                .syncWait();
    }
}
