package org.reactivetoolbox.core.meta;
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

import org.reactivetoolbox.core.scheduler.TaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application-wide meta info repository. It's intended for storing immutable data which should be
 * available to entire application, like serializers/deserializers, configuration, executors, etc.
 * The main purpose of this repository is to avoid spreading out this information across different
 * classes and simplify common configuration and maintenance of various pieces of data.
 * <br>
 * Unlike other parts of the toolbox, repository throws an exception in case if requested information
 * is not available. There are several reasons for this behavior, but the main one is that application
 * which is not properly configured should not be allowed to run in first place. There is no (at least
 * simple) way to shift this check to compile-time level, so run-time error approach is taken.
 * In order to make it work reliably, check for presence of necessary parts of meta information
 * should be done in application code at the very beginning of execution in order to prevent
 * application failure when it is already started.
 * <br>
 * Interface is designed with usual use case in mind: <code>start-configure-run-shutdown</code>. To support it
 * repository starts in <b>open</b> state, which allows adding necessary configuration into repository via
 * {@link #put(Class, Object)} calls. In this state no {@link #get(Class)} calls are allowed. Once configuration of
 * the repository is finished, application calls {@link #seal()} method. In <b>sealed</b> state, {@link #get(Class)}
 * calls are allowed, but any attempt to call {@link #put(Class, Object)} will throw an {@link IllegalStateException}.
 * Beside enforcing proper life cycle, this approach also enables use of non-synchronized data structures for
 * storing data at run-time which minimizes possible performance impact.
 */
public final class AppMetaRepository {
    private final Map<Class<?>, Object> meta = new HashMap<>();
    private final Map<Class<?>, Object> builder = new ConcurrentHashMap<>();

    /**
     * Get specified part of meta information.
     *
     * @param type
     *        The type of information required
     * @return associated instance
     * @throws IllegalStateException in case if called in open mode or requested information is missing
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        if (meta.isEmpty()) {
            throw new IllegalStateException("Attempt to retrieve " + type.getName() + " while in open state");
        }

        final var result = (T) meta.get(type);

        if (result == null) {
            throw new IllegalStateException("Metadata for " + type.getName() + " are not configured");
        }

        return result;
    }

    /**
     * Store specified part of meta information.
     *
     * @param type
     *        The type of meta information to store.
     * @param instance
     *        Value to store
     * @return <code>this</code> instance for fluent calls
     * @throws IllegalStateException if called in sealed mode
     */
    public <T> AppMetaRepository put(final Class<T> type, final T instance) {
        if (!meta.isEmpty()) {
            throw new IllegalStateException("Attempt to configure " + type.getName() + " in sealed state");
        }

        builder.put(type, instance);

        return this;
    }

    /**
     * Seal repository. All configured metadata is moved to work storage.
     */
    public AppMetaRepository seal() {
        if (meta.isEmpty()) {
            meta.putAll(builder);
            builder.clear();
        }
        return this;
    }

    public static AppMetaRepository instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final AppMetaRepository INSTANCE = new AppMetaRepository();

        //Pre-load default configuration for built-in classes
        static {
            final int workerSchedulerSize = Runtime.getRuntime().availableProcessors();

            INSTANCE.put(TaskScheduler.class, TaskScheduler.with(workerSchedulerSize));
        }
    }
}
