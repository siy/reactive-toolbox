package org.reactivetoolbox.json;

public interface Input {
    boolean hasNext();

    JsonToken next();
}
