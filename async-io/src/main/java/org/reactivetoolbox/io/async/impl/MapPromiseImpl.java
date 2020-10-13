package org.reactivetoolbox.io.async.impl;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MapPromiseImpl<T, S> implements Promise<T> {
    private final Promise<S> delegate;
    private final FN1<T,S> mapper;

    private MapPromiseImpl(final Promise<S> delegate, final FN1<T,S> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    public static <T, S> Promise<T> mapped(final Promise<S> delegate, final FN1<T,S> mapper) {
        return new MapPromiseImpl<>(delegate, mapper);
    }

    @Override
    public Promise<T> async(final Consumer<Promise<T>> task) {
        PromiseImpl.SingletonHolder.scheduler()
                                   .submit(() -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task) {
        PromiseImpl.SingletonHolder.scheduler()
                                   .submit(submitter -> submitter.delay(($1,$2) -> task.accept(this), timeout));
        return this;
    }

    @Override
    public Promise<T> async(final BiConsumer<Promise<T>, Submitter> task) {
        PromiseImpl.SingletonHolder.scheduler()
                                   .submit(submitter -> task.accept(this, submitter));
        return this;
    }

    @Override
    public Promise<T> resolve(final Result<T> result) {
        //TODO: how to implement it?
        return null;
    }

    @Override
    public Promise<T> syncResolve(final Result<T> result, final Submitter submitter) {
        //TODO: how to implement it?
        return null;
    }

    @Override
    public Promise<T> onResult(final Consumer<Result<T>> action) {
        delegate.onResult(s -> action.accept(s.map(mapper)));
        return this;
    }

    @Override
    public Promise<T> onResult(final Submitter submitter, final BiConsumer<Result<T>, Submitter> action) {
        delegate.onResult(submitter, (s, submitter1) -> action.accept(s.map(mapper), submitter1));
        return this;
    }

    @Override
    public Promise<T> onSuccess(final Consumer<T> action) {
        delegate.onSuccess(s -> action.accept(mapper.apply(s)));
        return this;
    }

    @Override
    public Promise<T> onFailure(final Consumer<? super Failure> action) {
        delegate.onFailure(action);
        return this;
    }

    @Override
    public void syncWait(final Consumer<Result<T>> handler) {
        delegate.syncWait(s -> handler.accept(s.map(mapper)));
    }

    @Override
    public void syncWait(final Timeout timeout, final Consumer<Result<T>> handler) {
        delegate.syncWait(timeout, s -> handler.accept(s.map(mapper)));
    }

    @Override
    public CoreLogger logger() {
        return delegate.logger();
    }
}
