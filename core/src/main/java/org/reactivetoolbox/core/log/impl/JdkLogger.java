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

package org.reactivetoolbox.core.log.impl;

import org.reactivetoolbox.core.log.CoreLogger;

import static java.lang.System.Logger.Level;

public class JdkLogger implements CoreLogger {
    private static final String LOGGER_NAME = "reactive-toolbox-logger";

    private final System.Logger logger;

    public JdkLogger() {
        logger = System.getLogger(LOGGER_NAME);
    }

    @Override
    public CoreLogger trace(final String msg) {
        logger.log(Level.TRACE, msg);
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Throwable throwable) {
        logger.log(Level.TRACE, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Object... params) {
        logger.log(Level.TRACE, msg, params);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg) {
        logger.log(Level.DEBUG, msg);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Throwable throwable) {
        logger.log(Level.DEBUG, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Object... params) {
        logger.log(Level.DEBUG, msg, params);
        return this;
    }

    @Override
    public CoreLogger info(final String msg) {
        logger.log(Level.INFO, msg);
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Throwable throwable) {
        logger.log(Level.INFO, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Object... params) {
        logger.log(Level.INFO, msg, params);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg) {
        logger.log(Level.WARNING, msg);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Throwable throwable) {
        logger.log(Level.WARNING, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Object... params) {
        logger.log(Level.WARNING, msg, params);
        return this;
    }

    @Override
    public CoreLogger error(final String msg) {
        logger.log(Level.ERROR, msg);
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Throwable throwable) {
        logger.log(Level.ERROR, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Object... params) {
        logger.log(Level.ERROR, msg, params);
        return this;
    }
}
