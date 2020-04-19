package org.reactivetoolbox.core.examples.async.domain;

import java.util.UUID;

public class User {

    public User.Id id() {
        return null;
    }

    public static class Id {
        private final UUID id;
        private Id(final UUID id) {
            this.id = id;
        }
    }
}
