package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

import java.util.Optional;

public interface EventBus {
    /**
     * Send message to listeners. This method immediately returns results of
     * message delivering (but not the results of message processing). The results of message
     * processing can be obtained from the {@link Promise} which is returned in
     * case of successful message delivering.
     *
     * @param event
     *        Event to send
     * @param <E>
     *        Error type of the result of message delivering
     * @param <R>
     *        Return type of the result of message delivering
     * @param <T>
     *        Message type
     * @return {@link Either} containing {@link Promise} in case of success or {@link RoutingError}
     *         with the cause of the failure
     */
    <E, R, T> Either<RoutingError, Promise<Either<E, R>>> send(final Envelope<T> event);

    /**
     * Add new {@link Router} to the {@link EventBus}. The router will be responsible for delivering
     * messages wrapped into provided {@link Envelope type}. Note that this is Envelope type
     * defines which {@link Router} will be taking care of delivering particular message.
     * Note that if router for this type of envelope already present, it is not replaced with the new one.
     *
     * @param envelopeType
     *        {@link Envelope} type to use
     * @param router
     *        {@link Router} instance which will handle messages wrapped into specified {@link Envelope}
     * @param <T>
     *        Type of the content inside the {@link Envelope}
     * @return <code>this</code> instance of {@link EventBus} for fluent syntax support
     */
    <T> EventBus router(final Class<Envelope<T>> envelopeType, final Router<T> router);

    /**
     * Add new {@link Router} to the {@link EventBus}. The router will be responsible for delivering
     * messages wrapped into provided {@link Envelope type}. Note that this is Envelope type
     * defines which {@link Router} will be taking care of delivering particular message.
     * Unlike {@link #router(Class, Router)} this method replaces existing router if any.
     *
     * @param envelopeType
     *        {@link Envelope} type to use
     * @param router
     *        {@link Router} instance which will handle messages wrapped into specified {@link Envelope}
     * @param <T>
     *        Type of the content inside the {@link Envelope}
     * @return <code>this</code> instance of {@link EventBus} for fluent syntax support
     */
    <T> EventBus upsertRouter(Class<Envelope<T>> envelopeType, Router<T> router);

    /**
     * Get instance of {@link Router} which is responsible to handling specified {@link Envelope} type
     *
     * @param envelopeType
     *        The type of the {@link Envelope}
     * @param <T>
     *        The type of the {@link Envelope} content
     * @return {@link Optional} which contains corresponding {@link Router} or is empty if no such
     * assignment is configured in this instance of {@link EventBus}
     */
    <T> Optional<Router<T>> router(final Class<Envelope<T>> envelopeType);
}
