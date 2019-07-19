package org.reactivetoolbox.build;

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

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Server;

public class ServerBuilder {
    private final Router<RequestContext> router;

    private ServerBuilder(final Router<RequestContext> router) {
        this.router = router;
    }

    public static ServerBuilder with(final Router<RequestContext> router) {
        return new ServerBuilder(router);
    }

    @SafeVarargs
    public static ServerBuilder with(final RouteBase<RequestContext>... routes) {
        return new ServerBuilder(Router.of(Option.empty(), routes));
    }

    @SafeVarargs
    public static ServerBuilder with(final String root, final RouteBase<RequestContext>... routes) {
        return new ServerBuilder(Router.of(Option.of(root), routes));
    }

    public Server build() {
        //TODO: implement
        return null;
    }
}
