package com.reuniondearte.api.article;

import com.reuniondearte.api.author.Author;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleResponseTest {

    @Test
    void publicArticleSummaryIncludesAuthorProfileLinks() {
        Author author = author(
                3L,
                "Francisco Manuel Luque Martínez",
                "francisco-manuel-luque-martinez",
                "https://franciscomluque.wixsite.com/escritor-francisco-m",
                "https://letterboxd.com/example/"
        );
        Article article = new Article();
        article.applyEditorialUpdate(
                "Articulo",
                "articulo",
                "Excerpt",
                "Contenido",
                ArticleStatus.published,
                author,
                null,
                OffsetDateTime.parse("2026-08-26T10:00:00+02:00"),
                null
        );
        article.replaceAuthors(List.of(author));

        ArticleSummaryResponse response = ArticleSummaryResponse.from(article);

        assertThat(response.authorDetails().websiteUrl()).isEqualTo("https://franciscomluque.wixsite.com/escritor-francisco-m");
        assertThat(response.authorDetails().letterboxdUrl()).isEqualTo("https://letterboxd.com/example/");
        assertThat(response.authors()).singleElement().satisfies(publicAuthor -> {
            assertThat(publicAuthor.websiteUrl()).isEqualTo("https://franciscomluque.wixsite.com/escritor-francisco-m");
            assertThat(publicAuthor.letterboxdUrl()).isEqualTo("https://letterboxd.com/example/");
        });
    }

    private Author author(Long id, String name, String slug, String websiteUrl, String letterboxdUrl) {
        Author author = new Author();
        ReflectionTestUtils.setField(author, "id", id);
        author.applyAdminUpdate(name, slug, "Colaborador", "Bio", null, websiteUrl, letterboxdUrl);
        return author;
    }
}
