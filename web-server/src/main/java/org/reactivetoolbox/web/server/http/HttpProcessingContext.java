package org.reactivetoolbox.web.server.http;

import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.parameter.conversion.ProcessingContext;

import java.nio.ByteBuffer;

public interface HttpProcessingContext extends ProcessingContext {
    Request request();

    Response response();

    Path path();

    <T> Option<T> contextComponent(Class<T> type);

    Functions.FN1<ByteBuffer, Object> resultSerializer();
}
