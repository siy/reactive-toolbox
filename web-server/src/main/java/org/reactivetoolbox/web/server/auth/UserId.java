package org.reactivetoolbox.web.server.auth;

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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

import java.util.UUID;

/**
 * Generic user Id suitable for various use cases
 */
public interface UserId {
    /**
     * Get user Id as string, possibly converting it from internal representation.
     *
     * @return String representing user Id.
     */
    String asString();

    /**
     * Get user Id as {@link UUID}, possibly converting it from internal representation.
     * If error occurred or implementation does not support such a conversion, then {@link Either} with failure is returned.
     *
     * @return {@link Either} with user Id converted to {@link UUID} or with failure
     */
    Either<? extends BaseError, UUID> asUUID();
}
