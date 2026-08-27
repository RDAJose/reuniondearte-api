package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleAuthor;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import com.reuniondearte.api.author.Author;
import com.reuniondearte.api.author.AuthorRepository;
import com.reuniondearte.api.category.Category;
import com.reuniondearte.api.category.CategoryRepository;
import com.reuniondearte.api.media.ArticleMediaRepository;
import com.reuniondearte.api.seo.SeoMetadata;
import com.reuniondearte.api.seo.SeoMetadataRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminArticleControllerTest {
    private final ArticleRepository articles = mock(ArticleRepository.class);
    private final AuthorRepository authors = mock(AuthorRepository.class);
    private final CategoryRepository categories = mock(CategoryRepository.class);
    private final SeoMetadataRepository seoMetadata = mock(SeoMetadataRepository.class);
    private final ArticleMediaRepository articleMedia = mock(ArticleMediaRepository.class);
    private final AdminArticleController controller = new AdminArticleController(
            articles,
            authors,
            categories,
            seoMetadata,
            articleMedia
    );

    @Test
    void createArticleKeepsUniqueMultipleAuthorsInRequestedOrder() {
        Author maria = author(2L, "María García Santiago", "maria-garcia-santiago");
        Author jose = author(1L, "José Luis Olmedo Barrionuevo", "jose-luis-olmedo");
        when(articles.findBySlug("coautoria")).thenReturn(Optional.empty());
        when(authors.findById(2L)).thenReturn(Optional.of(maria));
        when(authors.findById(1L)).thenReturn(Optional.of(jose));
        when(categories.findBySlug("cultura")).thenReturn(Optional.of(category()));
        when(articles.save(any(Article.class))).thenAnswer(invocation -> {
            Article saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });
        when(seoMetadata.findByArticleId(10L)).thenReturn(Optional.empty());
        when(seoMetadata.save(any(SeoMetadata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        controller.create(new AdminArticleRequest(
                "Coautoria",
                "coautoria",
                "Excerpt",
                "Contenido",
                "cultura",
                2L,
                List.of(2L, 1L, 2L),
                ArticleStatus.draft,
                null,
                null,
                null,
                null,
                false
        ));

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articles).save(articleCaptor.capture());
        Article saved = articleCaptor.getValue();
        assertThat(saved.getAuthor()).isSameAs(maria);
        assertThat(saved.getArticleAuthors())
                .extracting(ArticleAuthor::getAuthor)
                .containsExactly(maria, jose);
        assertThat(saved.getArticleAuthors())
                .extracting(ArticleAuthor::getPosition)
                .containsExactly(0, 1);
    }

    private Author author(Long id, String name, String slug) {
        Author author = new Author();
        ReflectionTestUtils.setField(author, "id", id);
        author.applyAdminUpdate(name, slug, "Colaborador", "Bio", null, null, null);
        return author;
    }

    private Category category() {
        Category category = new Category();
        ReflectionTestUtils.setField(category, "id", 1L);
        ReflectionTestUtils.setField(category, "slug", "cultura");
        ReflectionTestUtils.setField(category, "name", "Cultura");
        return category;
    }
}
