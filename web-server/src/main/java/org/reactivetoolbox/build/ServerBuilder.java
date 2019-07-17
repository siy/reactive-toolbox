package org.reactivetoolbox.build;

import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Server;

import java.util.Arrays;
import java.util.Collections;

public class ServerBuilder {
    private Router<RequestContext> router;

    public ServerBuilder withRouter(final Router<RequestContext> router) {
        this.router = router;
        return this;
    }

    @SafeVarargs
    public final ServerBuilder with(final RouteBase<RequestContext>... routes) {
        this.router = Router.of(routes);
        return this;
    }

    public Server build() {
        //TODO: implement
        return null;
    }
}
