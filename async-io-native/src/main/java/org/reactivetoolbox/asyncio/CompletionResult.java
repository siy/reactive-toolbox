package org.reactivetoolbox.asyncio;

public class CompletionResult<T> {
    private T userData;
    private long result;

    public T userData() {
        return userData;
    }

    public long result() {
        return result;
    }
}
