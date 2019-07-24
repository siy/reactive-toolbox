package org.reactivetoolbox.web.server.parameter.auth;

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

/**
 * Common interface for authentication data container.
 */
public interface Authentication {
    /**
     * Get user ID associated with this instance
     *
     * @return User ID
     */
    UserId userId();
    Either<? extends BaseError, Authentication> token();
    Either<? extends BaseError, Authentication> hasAllRoles(Role ... roles);
    Either<? extends BaseError, Authentication> hasAnyRoles(Role ... roles);
}
