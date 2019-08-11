package org.reactivetoolbox.build;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.RouteDescription;
import org.reactivetoolbox.value.conversion.VarDescription;

import java.util.List;

public class HttpRouteDescription implements RouteDescription {
    private final DescribedPath describedPath;
    private final List<Option<VarDescription>> parameterDescriptions;

    private HttpRouteDescription(final DescribedPath describedPath, final List<Option<VarDescription>> parameterDescriptions) {
        this.describedPath = describedPath;
        this.parameterDescriptions = parameterDescriptions;
    }

    public static RouteDescription of(final DescribedPath describedPath, final List<Option<VarDescription>> parameterDescriptions) {
        return new HttpRouteDescription(describedPath, parameterDescriptions);
    }

    @Override
    public Path path() {
        return describedPath.path();
    }

    @Override
    public RouteDescription path(final Option<String> root) {
        return of(describedPath.root(root), parameterDescriptions);
    }
}
