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

package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Result;

public class ResultExample {
    private static final Failure USER_NOT_FOUND = null;

    private final ResultUserService resultUserService;
    private final ResultUserProfileService resultUserProfileService;

    public ResultExample(final ResultUserService resultUserService,
                         final ResultUserProfileService resultUserProfileService) {
        this.resultUserService = resultUserService;
        this.resultUserProfileService = resultUserProfileService;
    }

    public Result<UserProfileResponse> getUserProfileHandler(final User.Id userId) {
        return resultUserService.findById(userId)
                                .flatMap(user -> resultUserProfileService.findById(userId)
                                                                         .map(profile -> UserProfileResponse.of(user, profile)));
    }
}
