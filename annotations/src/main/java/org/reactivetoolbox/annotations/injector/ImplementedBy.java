/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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
 *
 */

package org.reactivetoolbox.annotations.injector;

import java.lang.annotation.*;

/**
 * This annotation is used to provide reference to implementation from interface. For example:
 * <pre>{@code
 * @ImplementedBy(MyInterfaceImpl.class)
 * public interface MyInterface {
 *     ...
 * }
 *
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ImplementedBy {
    Class<?> value();
}
