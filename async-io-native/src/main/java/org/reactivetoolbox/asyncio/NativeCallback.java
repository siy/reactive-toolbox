package org.reactivetoolbox.asyncio;

import java.net.InetAddress;

public interface NativeCallback {
    void plainCompletion(long requestId, int result, int flags);

    void statCompletion(long requestId, int result, int flags, FileStatData data);

    void acceptCompletion(long requestId, int result, int flags, InetAddress clientAddress);
}
