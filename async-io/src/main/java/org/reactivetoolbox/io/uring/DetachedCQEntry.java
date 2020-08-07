package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;

public class DetachedCQEntry {
    private final int res;
    private final int flags;

    public DetachedCQEntry(final int res, final int flags) {
        this.res = res;
        this.flags = flags;
    }

    public int flags() {
        return flags;
    }

    public int res() {
        return res;
    }

    public <T> Result<T> result(final FN1<T, Integer> constructor) {
        return NativeError.result(res, constructor);
    }
}
