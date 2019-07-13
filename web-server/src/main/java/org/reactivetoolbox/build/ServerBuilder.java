package org.reactivetoolbox.build;

import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Server;

public class ServerBuilder {
    private Router<RequestContext> router;

    public ServerBuilder withRouter(final Router<RequestContext> router) {
        this.router = router;
        return this;
    }

    @SafeVarargs
    public final ServerBuilder withRoutes(final Route<RequestContext>... routes) {
        this.router = Router.of(routes);
        return this;
    }

    public Server build() {
        return null;
    }
}
