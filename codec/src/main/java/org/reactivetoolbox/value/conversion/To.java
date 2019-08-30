package org.reactivetoolbox.value.conversion;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class To {
    public static <T> Either<? extends BaseError, Option<Set<T>>> set(final Option<List<T>> input) {
        return Either.success(input.map(HashSet::new));
    }

    public static <T> Either<? extends BaseError, Option<Set<T>>> linkedSet(final Option<List<T>> input) {
        return Either.success(input.map(LinkedHashSet::new));
    }
}
