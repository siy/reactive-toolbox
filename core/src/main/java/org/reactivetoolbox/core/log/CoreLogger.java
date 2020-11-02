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

package org.reactivetoolbox.core.log;

public interface CoreLogger {
    CoreLogger trace(final String msg);
    CoreLogger trace(final String msg, final Throwable throwable);
    CoreLogger trace(final String msg, final Object... params);

    CoreLogger debug(final String msg);
    CoreLogger debug(final String msg, final Throwable throwable);
    CoreLogger debug(final String msg, final Object... params);

    CoreLogger info(final String msg);
    CoreLogger info(final String msg, final Throwable throwable);
    CoreLogger info(final String msg, final Object... params);

    CoreLogger warn(final String msg);
    CoreLogger warn(final String msg, final Throwable throwable);
    CoreLogger warn(final String msg, final Object... params);

    CoreLogger error(final String msg);
    CoreLogger error(final String msg, final Throwable throwable);
    CoreLogger error(final String msg, final Object... params);
}
