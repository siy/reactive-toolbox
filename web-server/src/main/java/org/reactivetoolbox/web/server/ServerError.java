package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.async.BaseError;

public enum ServerError implements BaseError {
    NOT_YET_RUNNING(500, "Server is not yet running and can't be stopped"),
    ALREADY_RUNNING(500, "Server is already running and can't be started again"),
    SERVER_FAILED_TO_START(500, "Server failed to start"),
    SERVER_FAILED_TO_STOP(500, "Server failed to stop"),
    BAD_REQUEST(404, "Invalid request path or method"),
    ;

    private final int code;
    private final String message;

    ServerError(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
