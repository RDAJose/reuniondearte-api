package com.reuniondearte.api.admin;

import com.reuniondearte.api.interaction.ArticleComment;
import com.reuniondearte.api.interaction.ArticleCommentRepository;
import com.reuniondearte.api.interaction.ArticleCommentStatus;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {
    private final ArticleCommentRepository comments;

    public AdminCommentController(ArticleCommentRepository comments) {
        this.comments = comments;
    }

    @GetMapping
    public List<AdminCommentResponse> list(@RequestParam(required = false) ArticleCommentStatus status) {
        List<ArticleComment> result = status == null
                ? comments.findAllByOrderByCreatedAtDesc()
                : comments.findByStatusOrderByCreatedAtDesc(status);
        return result.stream()
                .map(AdminCommentResponse::from)
                .toList();
    }

    @PatchMapping("/{id}/approve")
    @Transactional
    public AdminCommentResponse approve(@PathVariable Long id) {
        ArticleComment comment = commentOr404(id);
        comment.approve();
        return AdminCommentResponse.from(comments.save(comment));
    }

    @PatchMapping("/{id}/reject")
    @Transactional
    public AdminCommentResponse reject(@PathVariable Long id) {
        ArticleComment comment = commentOr404(id);
        comment.reject();
        return AdminCommentResponse.from(comments.save(comment));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ArticleComment comment = commentOr404(id);
        comments.delete(comment);
        return ResponseEntity.noContent().build();
    }

    private ArticleComment commentOr404(Long id) {
        return comments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }
}
