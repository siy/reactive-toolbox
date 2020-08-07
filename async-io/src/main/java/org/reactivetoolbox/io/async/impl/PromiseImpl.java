package org.reactivetoolbox.io.async.impl;

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

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.scheduler.TaskScheduler;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Implementation of {@link Promise}
 */
public class PromiseImpl<T> implements Promise<T> {
    private final AtomicReference<Result<T>> value = new AtomicReference<>(null);
//    private final CountDownLatch actionsHandled = new CountDownLatch(1);
    private final AtomicReference<Node<T>> head = new AtomicReference<>();

    private static final AtomicReference<Consumer<Throwable>> exceptionConsumer =
            new AtomicReference<>(e -> SingletonHolder.logger().debug("Exception while applying handlers", e));

    private static final class Node<T> {
        public Consumer<Result<T>> element;
        public Node<T> nextNode;

        public Node(final Consumer<Result<T>> element) {
            this.element = element;
        }
    }

    public static void exceptionConsumer(final Consumer<Throwable> consumer) {
        exceptionConsumer.set(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncResolve(final Result<T> result) {
        if (value.compareAndSet(null, result)) {
            handleActions();
        }

        return this;
    }

    @Override
    public Promise<T> resolve(final Result<T> result) {
        if (value.compareAndSet(null, result)) {
            handleActionsAsync();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> onResult(final Consumer<Result<T>> action) {
        //Note: this is very performance critical method, so internals are inlined
        //Warning: do not change unless you clearly understand what are you doing!

        final Consumer<Result<T>> safeAction = result -> {
            try {
                action.accept(result);
            } catch (final Throwable t) {
                exceptionConsumer.get().accept(t);
            }
        };

        if (value.get() != null) {
            safeAction.accept(value.get());
            return this;
        }

        final var newHead = new Node<>(safeAction);
        Node<T> oldHead;

        do {
            oldHead = head.get();
            newHead.nextNode = oldHead;
        } while (!head.compareAndSet(oldHead, newHead));

        if (value.get() != null) {
            handleActions();
        }

        return this;
    }

    private void handleActionsAsync() {
        SingletonHolder.scheduler()
                       .submit(this::handleActions);
    }

    private void handleActions() {
        //Note: this is very performance critical method, so internals are inlined
        //Warning: do not change unless you clearly understand what are you doing!
        final var result = value.get();

        boolean hasElements;
        do {
            Node<T> head;
            do {
                head = this.head.get();
            } while (!this.head.compareAndSet(head, null));

            Node<T> current = head;
            Node<T> prev = null;
            Node<T> next = null;

            while(current != null) {
                next = current.nextNode;
                current.nextNode = prev;
                prev = current;
                current = next;
            }

            hasElements = prev != null;

            while (prev != null) {
                prev.element.accept(result);
                prev = prev.nextNode;
            }

        } while (hasElements);

//        actionsHandled.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait() {
        final CountDownLatch actionsHandled = new CountDownLatch(1);
        thenDo(actionsHandled::countDown);

        try {
            actionsHandled.await();
        } catch (final Exception e) {
            logger().debug("Exception in syncWait()", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait(final Timeout timeout) {
        final CountDownLatch actionsHandled = new CountDownLatch(1);
        thenDo(actionsHandled::countDown);

        try {
            if (!actionsHandled.await(timeout.asMillis(), TimeUnit.MILLISECONDS)) {
                syncFail(Errors.TIMEOUT);
            }
        } catch (final Exception e) {
            logger().debug("Exception in syncWait(timeout)", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> async(final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler()
                       .submit(() -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler()
                       .submit(timeout, () -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final BiConsumer<Promise<T>, Submitter> task) {
        SingletonHolder.scheduler()
                       .submit((Submitter submitter) -> task.accept(this, submitter));
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Promise(", ")")
                .add(value.get() == null ? "<pending>" : value.get().toString())
                .toString();
    }

    @Override
    public CoreLogger logger() {
        return SingletonHolder.logger();
    }

//    private static final class SingletonHolder {
    public static final class SingletonHolder {
        private static final int WORKER_SCHEDULER_SIZE = Math.max(Runtime.getRuntime().availableProcessors() - 1, 2);

        private static final TaskScheduler SCHEDULER = AppMetaRepository.instance()
                                                                        .put(TaskScheduler.class,
                                                                             TaskScheduler.with(WORKER_SCHEDULER_SIZE))
                                                                        .get(TaskScheduler.class);

        private SingletonHolder() {
        }

        public static TaskScheduler scheduler() {
            return SCHEDULER;
        }

        public static CoreLogger logger() {
            return SCHEDULER.logger();
        }
    }
}
