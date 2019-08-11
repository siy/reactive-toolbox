package org.reactivetoolbox.core.scheduler;

/**
 * Interface for task submission.
 */
public interface Handle {
    /**
     * Submit task for execution after specified timeout
     *
     * @param timeout
     *        Execution timeout
     * @param runnable
     *        Task to execute
     * @return Current instance
     */
    Handle submit(Timeout timeout, Runnable runnable);

    /**
     * Release handle. Once handle is released, no tasks can be submitted.
     */
    void release();
}
