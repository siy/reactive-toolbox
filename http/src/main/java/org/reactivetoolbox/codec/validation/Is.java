package org.reactivetoolbox.codec.validation;

import org.reactivetoolbox.codec.json.CodecError;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;

public interface Is {
    static <T> Result<T> notNull(final Option<T> input) {
        return input.fold(v -> CodecError.error("Value must not be null"),
                         Result::ok);
    }
}
