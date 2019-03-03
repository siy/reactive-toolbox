/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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
 *
 */

package org.reactivetoolbox.injector;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.injector.InjectorException;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.reactivetoolbox.injector.Suppliers.*;

public class SuppliersTest {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int NUM_ITERATIONS = NUM_THREADS * 100;

    @Test
    public void measurePerformance() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        measure(lazy(counter::incrementAndGet), "(lambda lazy)");
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    public void shouldCallInitOnlyOnce() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        checkInstantiatedOnce(lazy(counter::incrementAndGet));
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionIfNullIsPassedToLazy() throws Exception {
        assertThrows(InjectorException.class, () -> lazy(null));
    }

    @Test
    public void shouldThrowExceptionIfNullIsPassedToFactoryLazy() throws Exception {
        assertThrows(InjectorException.class, () -> factoryLazy(null));
    }

    @Test
    public void shouldThrowExceptionIfNullIsPassedToSingleton() throws Exception {
        assertThrows(InjectorException.class, () -> singleton(null, false));
    }

    @Test
    public void shouldThrowExceptionIfNullIsPassedToEnhancing1() throws Exception {
        assertThrows(InjectorException.class, () -> enhancing(null, () -> () -> 1));
    }

    @Test
    public void shouldThrowExceptionIfNullIsPassedToEnhancing() throws Exception {
        assertThrows(InjectorException.class, () -> enhancing(() -> 1, null));
    }

    @Test
    public void shouldCreateLazySingleton() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        Supplier<Integer> supplier = singleton(counter::incrementAndGet, false);
        assertThat(counter.get()).isEqualTo(0);

        Integer value1 = supplier.get();
        assertThat(value1).isEqualTo(1);
        assertThat(counter.get()).isEqualTo(1);

        Integer value2 = supplier.get();
        assertThat(value2).isEqualTo(1);
        assertThat(counter.get()).isEqualTo(1);

        assertThat(value1).isSameAs(value2);
    }

    @Test
    public void shouldProgressivelyEnhance() throws Exception {
        Supplier<Integer> supplier = enhancing(() -> 1, () -> () -> 2);

        assertThat(supplier.get()).isEqualTo(1);
        assertThat(supplier.get()).isEqualTo(1);
        assertThat(supplier.get()).isEqualTo(1);
        assertThat(supplier.get()).isEqualTo(2);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void measure(Supplier<Integer> supplier, String type) throws InterruptedException, java.util.concurrent.ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

        List<Callable<Integer>> callables = IntStream.range(0, NUM_THREADS)
                                                     .mapToObj(n -> (Callable<Integer>) supplier::get)
                                                     .collect(Collectors.toList());

        long start = System.nanoTime();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            for (Future<Integer> future : pool.invokeAll(callables)) {
                assertThat(future.get()).isEqualTo(1);
            }
        }
        System.out.printf("Time %s : %.2fms\n",  type, (System.nanoTime() - start)/1000000.0);
    }

    private void checkInstantiatedOnce(Supplier<Integer> supplier) throws InterruptedException, java.util.concurrent.ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
        @SuppressWarnings("SpellCheckingInspection") List<Callable<Integer>> callables = IntStream.range(0, NUM_THREADS)
                                                     .mapToObj(n -> (Callable<Integer>) () -> {
                                                         barrier.await();
                                                         return supplier.get();
                                                     })
                                                     .collect(Collectors.toList());

        for(Future<Integer> future : pool.invokeAll(callables)) {
            assertThat(future.get()).isEqualTo(1);
        }
    }
}
