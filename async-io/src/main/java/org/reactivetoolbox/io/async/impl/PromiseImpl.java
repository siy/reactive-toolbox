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
import org.reactivetoolbox.io.async.util.BooleanLatch;
import org.reactivetoolbox.io.scheduler.TaskScheduler;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Implementation of {@link Promise}
 */
public class PromiseImpl<T> implements Promise<T> {
    private volatile Result<T> value;
    private volatile Node<T> head;
    private final BooleanLatch actionsHandled = new BooleanLatch();

    private static final VarHandle VALUE;
    private static final VarHandle HEAD;

    static {
        try {
            final MethodHandles.Lookup l = MethodHandles.lookup();
            VALUE = l.findVarHandle(PromiseImpl.class, "value", Result.class);
            HEAD = l.findVarHandle(PromiseImpl.class, "head", Node.class);
        } catch (final ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

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
        if (VALUE.compareAndSet(this, null, result)) {
            handleActions();
        }

        return this;
    }

    @Override
    public Promise<T> resolve(final Result<T> result) {
        if (VALUE.compareAndSet(this, null, result)) {
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

        if (value != null) {
            safeAction.accept(value);
            return this;
        }

        final var newHead = new Node<>(safeAction);
        Node<T> oldHead;

        do {
            oldHead = head;
            newHead.nextNode = oldHead;
        } while (!HEAD.compareAndSet(this, oldHead, newHead));

        if (value != null) {
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
        //final var result = value.get();

        boolean hasElements;
        do {
            Node<T> head;
            do {
                head = this.head;
            } while (!HEAD.compareAndSet(this, head, null));

            Node<T> current = head;
            Node<T> prev = null;
            Node<T> next;

            while (current != null) {
                next = current.nextNode;
                current.nextNode = prev;
                prev = current;
                current = next;
            }

            hasElements = prev != null;

            while (prev != null) {
                prev.element.accept(value);
                prev = prev.nextNode;
            }

        } while (hasElements);

        actionsHandled.signal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait() {
        if(!actionsHandled.await()) {
            logger().debug("syncWait() was interrupted");
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait(final Timeout timeout) {
        if (!actionsHandled.await(timeout)) {
            syncFail(Errors.TIMEOUT);
            logger().debug("syncWait(Timeout) expired or interrupted");
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
                .add(value == null ? "<pending>" : value.toString())
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
