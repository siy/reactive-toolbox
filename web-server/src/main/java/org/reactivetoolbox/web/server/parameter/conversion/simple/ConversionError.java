package org.reactivetoolbox.web.server.parameter.conversion.simple;

import org.reactivetoolbox.core.async.BaseError;

public enum ConversionError implements BaseError {
    NOT_AN_INTEGER_NUMBER, NOT_A_LONG_INTEGER_NUMBER, NOT_A_DOUBLE_NUMBER, NOT_A_VALID_UUID, NOT_A_NUMBER
}
