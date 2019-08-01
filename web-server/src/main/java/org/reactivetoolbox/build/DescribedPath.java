package org.reactivetoolbox.build;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Path;

public class DescribedPath {
    private final Path path;
    private final String description;

    private DescribedPath(final Path path, final String description) {
        this.path = path;
        this.description = description;
    }

    public static DescribedPath of(final Path path, final String description) {
        return new DescribedPath(path, description);
    }

    public Path path() {
        return path;
    }

    public DescribedPath root(final Option<String> root) {
        return of(path.root(root), description);
    }
}
