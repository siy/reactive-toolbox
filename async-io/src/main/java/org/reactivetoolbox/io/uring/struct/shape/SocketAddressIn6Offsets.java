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

package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface SocketAddressIn6Offsets {
    int SIZE = 28;
    RawProperty sin6_family = raw(0, 2);
    RawProperty sin6_addr = raw(8, 16);
    RawProperty sin6_flowinfo = raw(4, 4);
    RawProperty sin6_port = raw(2, 2);
    RawProperty sin6_scope_id = raw(24, 4);
}
