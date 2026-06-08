package com.reuniondearte.api.admin;

import com.reuniondearte.api.author.AuthorRepository;
import com.reuniondearte.api.author.AuthorResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
