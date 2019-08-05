package org.reactivetoolbox.json;

import org.reactivetoolbox.core.functional.Option;

public interface JsonObject {
    Option<JsonObject> get(final String name);
}
