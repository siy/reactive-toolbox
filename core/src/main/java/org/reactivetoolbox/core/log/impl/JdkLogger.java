package org.reactivetoolbox.core.log.impl;

import org.reactivetoolbox.core.log.CoreLogger;

import static java.lang.System.Logger.Level;

public class JdkLogger implements CoreLogger {
    private static final String LOGGER_NAME = "reactive-toolbox-logger";

    private final System.Logger logger;

    public JdkLogger() {
        logger = System.getLogger(LOGGER_NAME);
    }

    @Override
    public CoreLogger trace(final String msg) {
        logger.log(Level.TRACE, msg);
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Throwable throwable) {
        logger.log(Level.TRACE, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Object... params) {
        logger.log(Level.TRACE, msg, params);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg) {
        logger.log(Level.DEBUG, msg);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Throwable throwable) {
        logger.log(Level.DEBUG, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Object... params) {
        logger.log(Level.DEBUG, msg, params);
        return this;
    }

    @Override
    public CoreLogger info(final String msg) {
        logger.log(Level.INFO, msg);
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Throwable throwable) {
        logger.log(Level.INFO, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Object... params) {
        logger.log(Level.INFO, msg, params);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg) {
        logger.log(Level.WARNING, msg);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Throwable throwable) {
        logger.log(Level.WARNING, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Object... params) {
        logger.log(Level.WARNING, msg, params);
        return this;
    }

    @Override
    public CoreLogger error(final String msg) {
        logger.log(Level.ERROR, msg);
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Throwable throwable) {
        logger.log(Level.ERROR, msg, throwable);
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Object... params) {
        logger.log(Level.ERROR, msg, params);
        return this;
    }
}
