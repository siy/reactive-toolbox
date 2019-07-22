package org.reactivetoolbox.build;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.RouteDescription;
import org.reactivetoolbox.web.server.parameter.ParameterDescription;

import java.util.List;

public class HttpRouteDescription implements RouteDescription {
    private final Path path;
    private final String description;
    private final List<Option<ParameterDescription>> parameterDescriptions;

    private HttpRouteDescription(final Path path, final String description, final List<Option<ParameterDescription>> parameterDescriptions) {
        this.path = path;
        this.description = description;
        this.parameterDescriptions = parameterDescriptions;
    }

    public static RouteDescription of(final Path path, final String description, final List<Option<ParameterDescription>> parameterDescriptions) {
        return new HttpRouteDescription(path, description, parameterDescriptions);
    }
}
