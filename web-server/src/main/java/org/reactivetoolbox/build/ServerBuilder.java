package org.reactivetoolbox.build;

import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.impl.RouterImpl;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Server;

public class ServerBuilder {
    private Router<RequestContext> router;

    public ServerBuilder withRouter(final Router<RequestContext> router) {
        this.router = router;
        return this;
    }

    public ServerBuilder withRoutes(final Route<RequestContext>... routes) {
        this.router = new RouterImpl<RequestContext>().with(routes);
        return this;
    }

    public Server build() {
        return null;
    }
}
