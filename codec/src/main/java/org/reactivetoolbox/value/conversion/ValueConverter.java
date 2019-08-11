package org.reactivetoolbox.value.conversion;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;

public interface ValueConverter<T> extends FN1<Either<? extends BaseError, Option<T>>, Option<String>> {}
