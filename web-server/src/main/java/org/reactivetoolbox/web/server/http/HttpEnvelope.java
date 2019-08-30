package org.reactivetoolbox.web.server.http;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;

import static org.reactivetoolbox.web.server.ServerError.BAD_REQUEST;

public class HttpEnvelope implements Envelope<HttpProcessingContext> {
    private final HttpProcessingContext context;
    private final Path path;

    private HttpEnvelope(final Path path, final HttpProcessingContext context) {
        this.context = context;
        this.path = path;
    }

    public static Envelope<HttpProcessingContext> of(final Path path, final HttpProcessingContext context) {
        return new HttpEnvelope(path, context);
    }

    @Override
    public Either<? extends BaseError, HttpProcessingContext> onDelivery(final Route<HttpProcessingContext> route) {
        final var parameters = route.path().extractParameters(path.source());

        return (route.path().hasParams() && parameters.isEmpty())
               ? BAD_REQUEST.asFailure()
               : Either.success(context.request()
                                       .pathParameters(parameters)
                                       .context());
    }

    @Override
    public Path target() {
        return path;
    }

    @Override
    public HttpProcessingContext payload() {
        return context;
    }
}
