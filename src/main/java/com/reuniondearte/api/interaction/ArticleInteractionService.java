package com.reuniondearte.api.interaction;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArticleInteractionService {
    private final ArticleRepository articles;
    private final ArticleLikeRepository likes;
    private final ArticleCommentRepository comments;

    public ArticleInteractionService(
            ArticleRepository articles,
            ArticleLikeRepository likes,
            ArticleCommentRepository comments
    ) {
        this.articles = articles;
        this.likes = likes;
        this.comments = comments;
    }

    public Article publicArticleOr404(String slug) {
        return articles.findBySlugAndStatus(slug, ArticleStatus.published)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    public ArticleInteractionSummary summary(String slug) {
        Article article = publicArticleOr404(slug);
        return new ArticleInteractionSummary(
                likes.countByArticleId(article.getId()),
                comments.countByArticleIdAndStatus(article.getId(), ArticleCommentStatus.APPROVED)
        );
    }

    @Transactional
    public ArticleLikeResponse setLiked(String slug, String clientId, boolean liked) {
        Article article = publicArticleOr404(slug);
        String visitorKeyHash = hashVisitorKey(article.getId(), clientId);
        likes.findByArticleIdAndVisitorKeyHash(article.getId(), visitorKeyHash).ifPresentOrElse(existing -> {
            if (!liked) {
                likes.delete(existing);
                likes.flush();
            }
        }, () -> {
            if (liked) {
                likes.save(new ArticleLike(article, visitorKeyHash));
            }
        });
        boolean currentLiked = likes.findByArticleIdAndVisitorKeyHash(article.getId(), visitorKeyHash).isPresent();
        return new ArticleLikeResponse(currentLiked, likes.countByArticleId(article.getId()));
    }

    private String hashVisitorKey(Long articleId, String clientId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((articleId + ":" + clientId).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
