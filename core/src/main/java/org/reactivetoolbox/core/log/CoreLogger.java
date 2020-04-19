package org.reactivetoolbox.core.log;

public interface CoreLogger {
    CoreLogger trace(final String msg);
    CoreLogger trace(final String msg, final Throwable throwable);
    CoreLogger trace(final String msg, final Object... params);

    CoreLogger debug(final String msg);
    CoreLogger debug(final String msg, final Throwable throwable);
    CoreLogger debug(final String msg, final Object... params);

    CoreLogger info(final String msg);
    CoreLogger info(final String msg, final Throwable throwable);
    CoreLogger info(final String msg, final Object... params);

    CoreLogger warn(final String msg);
    CoreLogger warn(final String msg, final Throwable throwable);
    CoreLogger warn(final String msg, final Object... params);

    CoreLogger error(final String msg);
    CoreLogger error(final String msg, final Throwable throwable);
    CoreLogger error(final String msg, final Object... params);
}
