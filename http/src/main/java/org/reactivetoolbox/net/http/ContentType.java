package org.reactivetoolbox.net.http;

import java.util.function.Supplier;

public interface ContentType extends Supplier<String> {
    ContentType TEXT_PLAIN = () -> "text/plain; charset=UTF-8";
    ContentType JSON = () -> "application/json; charset=UTF-8";
}
