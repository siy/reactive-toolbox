package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;

public interface Deserializer<T> extends FN1<Result<Option<T>>, Token> {
}
