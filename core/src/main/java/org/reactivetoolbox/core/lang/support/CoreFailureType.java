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

package org.reactivetoolbox.core.lang.support;

import org.reactivetoolbox.core.lang.functional.FailureType;

public enum CoreFailureType implements FailureType {
    TIMEOUT(1, "Processing timeout"),
    CANCELLED(2, "Processing cancelled"),
    UNKNOWN_TYPE(3, "The type is unknown"),
    INVALID_VALUE(4, "The passed value is invalid");

    private final int code;
    private final String description;

    CoreFailureType(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int typeCode() {
        return code;
    }

    @Override
    public String description() {
        return description;
    }
}
