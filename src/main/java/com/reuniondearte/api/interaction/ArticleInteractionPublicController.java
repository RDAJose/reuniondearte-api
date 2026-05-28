package com.reuniondearte.api.interaction;

import com.reuniondearte.api.article.Article;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/articles/{slug}")
public class ArticleInteractionPublicController {
    private static final String PENDING_MESSAGE = "Comentario recibido. Queda pendiente de moderacion.";

    private final ArticleInteractionService interactions;
    private final ArticleCommentRepository comments;

    public ArticleInteractionPublicController(
            ArticleInteractionService interactions,
            ArticleCommentRepository comments
    ) {
        this.interactions = interactions;
        this.comments = comments;
    }

    @GetMapping("/interactions")
    public ArticleInteractionSummary interactions(@PathVariable String slug) {
        return this.interactions.summary(slug);
    }

    @PostMapping("/likes")
    public ArticleLikeResponse like(@PathVariable String slug, @Valid @RequestBody ArticleLikeRequest request) {
        return interactions.setLiked(slug, request.clientId().trim(), request.liked());
    }

    @GetMapping("/comments")
    public List<ArticleCommentResponse> comments(@PathVariable String slug) {
        Article article = interactions.publicArticleOr404(slug);
        return comments.findByArticleIdAndStatusOrderByCreatedAtDesc(article.getId(), ArticleCommentStatus.APPROVED)
                .stream()
                .map(ArticleCommentResponse::from)
                .toList();
    }

    @PostMapping("/comments")
    @Transactional
    public ResponseEntity<ArticleCommentSubmitResponse> comment(
            @PathVariable String slug,
            @RequestBody ArticleCommentRequest request
    ) {
        Article article = interactions.publicArticleOr404(slug);
        if (request.website() != null && !request.website().isBlank()) {
            return ResponseEntity.accepted().body(new ArticleCommentSubmitResponse(PENDING_MESSAGE));
        }
        validateComment(request);
        if (!Boolean.TRUE.equals(request.consentAccepted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "consentAccepted must be true");
        }
        comments.save(new ArticleComment(
                article,
                request.publicName().trim(),
                request.body().trim(),
                true
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ArticleCommentSubmitResponse(PENDING_MESSAGE));
    }

    private void validateComment(ArticleCommentRequest request) {
        if (request.publicName() == null || request.publicName().isBlank() || request.publicName().trim().length() > 80) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "publicName is required and must be 1-80 characters");
        }
        if (request.body() == null || request.body().isBlank() || request.body().trim().length() > 1500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "body is required and must be 1-1500 characters");
        }
    }
}
