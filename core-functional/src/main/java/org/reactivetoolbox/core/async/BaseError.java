package org.reactivetoolbox.core.async;

/**
 * Marker interface for error types.
 *
 * @author Sergiy Yevtushenko
 */
public interface BaseError {
    //TODO: perhaps we need to move these methods up in the hierarchy
    default int code() {
        return 200;
    }

    default String message() {
        return "Success";
    }
}
