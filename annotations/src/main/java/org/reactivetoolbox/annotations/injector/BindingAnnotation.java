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
 * This is a special annotation which is used to create binding annotations from regular annotations.
 * Binding annotations are used to distinguish dependencies of same formal type but with different purposes.
 * For example:
 * <pre>{@code
 * ...
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target({PARAMETER, FIELD})
 * @Documented
 * @BindingAnnotation
 * public @interface MyAnnotation {
 * }
 * ...
 *
 * @ConfiguredBy(MyModule.class)
 * public class MyClass {
 *   ...
 *   public MyClass(@MyAnnotation List&lt;String&gt; names) {
 *     ...
 *   }
 *   ...
 * }
 * ...
 * public class MyModule {
 *   ...
 *   @Supplies
 *   @MyAnnotation
 *   public List&lt;String&gt; getServiceNames() {
 *      ...
 *   }
 *   ...
 * }
 *
 * ...
 * }</pre>
 *
 * @see Supplies
 * @see ConfiguredBy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Documented
public @interface BindingAnnotation {
}
