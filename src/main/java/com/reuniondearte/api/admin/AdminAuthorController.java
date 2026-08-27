package com.reuniondearte.api.admin;

import com.reuniondearte.api.author.Author;
import com.reuniondearte.api.author.AuthorRepository;
import com.reuniondearte.api.author.AuthorResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/authors")
public class AdminAuthorController {
    private final AuthorRepository authors;

    public AdminAuthorController(AuthorRepository authors) {
        this.authors = authors;
    }

    @GetMapping
    public List<AuthorResponse> listAuthors() {
        return authors.findAllByOrderByNameAsc().stream()
                .map(AuthorResponse::from)
                .toList();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AdminAuthorRequest request) {
        ensureSlugAvailable(request.slug(), null);
        Author author = new Author();
        apply(author, request);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(AuthorResponse.from(authors.saveAndFlush(author)));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateSlug();
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public AuthorResponse updateAuthor(@PathVariable Long id, @Valid @RequestBody AdminAuthorRequest request) {
        Author author = authors.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        ensureSlugAvailable(request.slug(), id);
        apply(author, request);
        try {
            return AuthorResponse.from(authors.saveAndFlush(author));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateSlug();
        }
    }

    private void ensureSlugAvailable(String slug, Long currentAuthorId) {
        authors.findBySlug(slug)
                .filter(existing -> currentAuthorId == null || !existing.getId().equals(currentAuthorId))
                .ifPresent(existing -> {
                    throw duplicateSlug();
                });
    }

    private void apply(Author author, AdminAuthorRequest request) {
        author.applyAdminUpdate(
                request.name(),
                request.slug(),
                request.role(),
                blankToNull(request.bio()),
                blankToNull(request.avatarUrl()),
                blankToNull(request.websiteUrl()),
                blankToNull(request.letterboxdUrl())
        );
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private ResponseStatusException duplicateSlug() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Author slug already exists");
    }
}
