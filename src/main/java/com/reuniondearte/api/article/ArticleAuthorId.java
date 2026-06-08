package com.reuniondearte.api.article;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArticleAuthorId implements Serializable {
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "author_id")
    private Long authorId;

    public ArticleAuthorId() {
    }

    public ArticleAuthorId(Long articleId, Long authorId) {
        this.articleId = articleId;
        this.authorId = authorId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ArticleAuthorId that)) {
            return false;
        }
        return Objects.equals(articleId, that.articleId) && Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, authorId);
    }
}
