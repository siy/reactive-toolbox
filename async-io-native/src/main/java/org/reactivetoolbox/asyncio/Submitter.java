package org.reactivetoolbox.asyncio;

public interface Submitter {
    public Submitter nop(final Runnable onCompletion);

}
