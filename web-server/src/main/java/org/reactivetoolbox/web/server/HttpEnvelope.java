package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;

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
    public Either<? extends BaseError, RequestContext> onDelivery() {
        return Either.success(context);
    }

    @Override
    public Path target() {
        return path;
    }
}
