package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Option;

import java.util.List;

public interface RouteBase<T> {
    Route<T> asRoute();

    Option<String> routeDescription();
    List<String> parameterDescription();
}
