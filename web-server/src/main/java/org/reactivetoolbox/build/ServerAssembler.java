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

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.Server;
import org.reactivetoolbox.web.server.adapter.ServerAdapter;
import org.reactivetoolbox.web.server.http.HttpProcessingContext;

/**
 * HTTP server fluent assembler.
 */
public class ServerAssembler {
    private final Router<HttpProcessingContext> router;

    private ServerAssembler(final Router<HttpProcessingContext> router) {
        this.router = router;
    }

    public static ServerAssembler with(final Router<HttpProcessingContext> router) {
        return new ServerAssembler(router);
    }

    @SafeVarargs
    public static ServerAssembler with(final RouteBase<HttpProcessingContext>... routes) {
        return new ServerAssembler(Router.of(Option.empty(), routes));
    }

    @SafeVarargs
    public static ServerAssembler with(final String root, final RouteBase<HttpProcessingContext>... routes) {
        return new ServerAssembler(Router.of(Option.of(root), routes));
    }

    public Server build(final FN1<ServerAdapter, Router<HttpProcessingContext>> factory) {
        return Server.of(router, factory.apply(router));
    }
}
