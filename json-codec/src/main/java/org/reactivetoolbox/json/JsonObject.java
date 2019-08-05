package org.reactivetoolbox.json;

import org.reactivetoolbox.core.functional.Option;

public interface JsonObject {
    Option<String> get(final String name);
}
