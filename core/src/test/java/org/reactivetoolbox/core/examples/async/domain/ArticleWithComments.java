package org.reactivetoolbox.core.examples.async.domain;

import java.util.List;

public class ArticleWithComments {
    private final Article article;
    private final List<Comment> comments;

    public ArticleWithComments(final Article article, final List<Comment> comments) {
        this.article = article;
        this.comments = comments;
    }
}
