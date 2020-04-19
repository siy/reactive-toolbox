package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.core.lang.functional.Functions;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.Tuple;

@FunctionalInterface
public interface CrossParameterValidator<T extends Tuple> extends Functions.FN1<Result<T>, T> {
}
