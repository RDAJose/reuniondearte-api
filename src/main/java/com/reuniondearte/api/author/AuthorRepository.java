package com.reuniondearte.api.author;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findBySlug(String slug);

    @EntityGraph(attributePaths = {"avatarMedia"})
    List<Author> findAllByOrderByNameAsc();
}
