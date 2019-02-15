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
 * This annotation is used to mark methods in configuration class which will supply instances of specified type.
 * For example:
 *
 * <pre>{@code
 * ...
 *  @Supplies
 *  public MyService<Notification> createNotificationService() {
 *  ...
 *  }
 * ...
 * }</pre>
 *
 * The return type of method is the exact type of the binding. If method has any binding annotation, it will be attached
 * to return type as well. If method has parameters, they will be injected during method invocation.
 *
 * @see ConfiguredBy
 * @see BindingAnnotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Supplies {
}
