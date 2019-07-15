package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.async.BaseError;

public enum AuthenticationError implements BaseError {
    USER_NOT_LOGGED_IN,
    USER_HAS_NOT_ENOUGH_PRIVILEGES,
    ;
}
