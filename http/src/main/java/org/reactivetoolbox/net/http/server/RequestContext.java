package org.reactivetoolbox.net.http.server;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.net.http.Method;

public interface RequestContext {
    NativeBuffer allocate();

    RequestContext preparePathContext(final FN1<ParsingContext, String> extractor);

    ParsingContext pathContext();
    ParsingContext queryContext();
    ParsingContext bodyContext();
    ParsingContext headerContext();

    String path();

    Method method();
}
