package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Pair;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;

import java.util.List;

public class HttpEnvelope implements Envelope<RequestContext> {
    private final RequestContext context;
    private final Path path;

    private HttpEnvelope(final Path path, final RequestContext context) {
        this.context = context;
        this.path = path;
    }

    public static Envelope<RequestContext> of(final Path path, final RequestContext context) {
        return new HttpEnvelope(path, context);
    }

    @Override
    public Either<? extends BaseError, RequestContext> onDelivery(final Route<RequestContext> route) {
        final List<Pair<String, String>> pairs = route.path().extractParameters(path.source());

        return (route.path().hasParams() && pairs.isEmpty())
               ? Either.failure(ServerError.BAD_REQUEST)
               : Either.success(context.request().pathParameters(pairs).context());
    }

    @Override
    public Path target() {
        return path;
    }

    @Override
    public RequestContext payload() {
        return context;
    }
}
