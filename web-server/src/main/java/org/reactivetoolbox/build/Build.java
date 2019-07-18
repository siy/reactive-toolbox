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

import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.Routes;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.RequestContext;

//TODO: Javadoc
public final class Build {
    private Build() {}

    public static ServerBuilder server() {
        return new ServerBuilder();
    }

    public static HttpRouteBuilder when(final HttpMethod method, final String path) {
        return HttpRouteBuilder.create(method, path);
    }

    @SafeVarargs
    public static Routes<RequestContext> with(final String root, final RouteBase<RequestContext> ... routes) {
        //TODO: add support for rooting inner routes at given root
        return Routes.of(routes);
    }
}
