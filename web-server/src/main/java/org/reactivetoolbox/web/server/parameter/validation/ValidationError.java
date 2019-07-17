package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;

public enum ValidationError implements BaseError {
    STRING_IS_NULL, STRING_IS_EMPTY,
    STRING_TOO_SHORT,
    STRING_TOO_LONG,
    NUMBER_IS_BELOW_LOWER_BOUND,
    NUMBER_IS_ABOVE_UPPER_BOUND,
    USER_NOT_LOGGED_IN,
    USER_HAS_NOT_ENOUGH_PRIVILEGES,
    ;
}
