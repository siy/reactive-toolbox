package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;

public class ResultProcessor {
    public static void processSuccess(final RequestContext context, final Object result) {
        //TODO: serialize result and finish processing of request
    }

    public static <NF> Promise<NF> processFailure(final RequestContext context, final BaseError requestProcessingError) {
        //TODO: serialize failure and finish processing of request
        return null;
    }
}
