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

public class NopLogger implements CoreLogger {
    @Override
    public CoreLogger trace(final String msg) {
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Object... params) {
        return this;
    }
}
