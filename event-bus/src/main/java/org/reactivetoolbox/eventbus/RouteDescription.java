package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Option;

public interface RouteDescription {
    Path path();

    RouteDescription path(Option<String> root);
}
