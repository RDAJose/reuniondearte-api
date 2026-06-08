package com.reuniondearte.api.article;

import com.reuniondearte.api.author.Author;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "article_authors")
public class ArticleAuthor {
    @EmbeddedId
    private ArticleAuthorId id = new ArticleAuthorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorId")
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(nullable = false)
    private Integer position = 0;

    public ArticleAuthor() {
    }

    public ArticleAuthor(Article article, Author author, Integer position) {
        this.article = article;
        this.author = author;
        this.position = position;
        this.id = new ArticleAuthorId(
                article == null ? null : article.getId(),
                author == null ? null : author.getId()
        );
    }

    public Author getAuthor() {
        return author;
    }

    public Integer getPosition() {
        return position;
    }
}
