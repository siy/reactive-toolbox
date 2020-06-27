package org.reactivetoolbox.io.async.lifecycle;

public enum Repeat implements Terminable {
    REPEAT,
    END;

    @Override
    public boolean isTerminal() {
        return this == END;
    }
}
