package com.reuniondearte.api.revision;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "article_revisions")
public class ArticleRevision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(nullable = false, length = 260)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(columnDefinition = "TEXT")
    private String contentHtml;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String changeNote;

    private OffsetDateTime createdAt;
}

