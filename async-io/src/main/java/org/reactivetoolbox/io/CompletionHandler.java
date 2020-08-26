package org.reactivetoolbox.io;

import org.reactivetoolbox.io.async.Submitter;

public interface CompletionHandler {
    void accept(int result, int flags, final Submitter submitter);
}
