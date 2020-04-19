package org.reactivetoolbox.core.log.impl;

import org.reactivetoolbox.core.log.CoreLogger;

public class NopLogger implements CoreLogger {
    @Override
    public CoreLogger trace(final String msg) {
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger trace(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger debug(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger info(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger warn(final String msg, final Object... params) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Throwable throwable) {
        return this;
    }

    @Override
    public CoreLogger error(final String msg, final Object... params) {
        return this;
    }
}
