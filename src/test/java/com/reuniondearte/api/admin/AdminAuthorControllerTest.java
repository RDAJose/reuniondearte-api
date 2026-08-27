package com.reuniondearte.api.admin;

import com.reuniondearte.api.author.Author;
import com.reuniondearte.api.author.AuthorRepository;
import com.reuniondearte.api.config.GlobalExceptionHandler;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAuthorControllerTest {
    private final AuthorRepository authors = mock(AuthorRepository.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminAuthorController(authors))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator())
            .build();

    @Test
    void listAuthorsReturnsKnownProfileLinks() throws Exception {
        when(authors.findAllByOrderByNameAsc()).thenReturn(List.of(
                author(1L, "José Luis Olmedo Barrionuevo", "jose-luis-olmedo", null, "https://letterboxd.com/rdajose/"),
                author(2L, "María García Santiago", "maria-garcia-santiago", null, "https://letterboxd.com/mariasantisima/")
        ));

        mockMvc.perform(get("/api/admin/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("jose-luis-olmedo"))
                .andExpect(jsonPath("$[0].letterboxdUrl").value("https://letterboxd.com/rdajose/"))
                .andExpect(jsonPath("$[1].slug").value("maria-garcia-santiago"))
                .andExpect(jsonPath("$[1].letterboxdUrl").value("https://letterboxd.com/mariasantisima/"));
    }

    @Test
    void createAuthorPersistsSanitizedRequest() throws Exception {
        when(authors.findBySlug("francisco-manuel-luque-martinez")).thenReturn(Optional.empty());
        when(authors.saveAndFlush(any(Author.class))).thenAnswer(invocation -> {
            Author saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 3L);
            return saved;
        });

        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " Francisco Manuel Luque Martínez ",
                                  "slug": " francisco-manuel-luque-martinez ",
                                  "role": " Escritor, guionista y colaborador en Reunión de Arte ",
                                  "bio": " Biografia ",
                                  "avatarUrl": " /authors/francisco-manuel-luque-martinez.jpeg ",
                                  "websiteUrl": " https://franciscomluque.wixsite.com/escritor-francisco-m ",
                                  "letterboxdUrl": ""
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.slug").value("francisco-manuel-luque-martinez"))
                .andExpect(jsonPath("$.websiteUrl").value("https://franciscomluque.wixsite.com/escritor-francisco-m"));

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authors).saveAndFlush(authorCaptor.capture());
        Author saved = authorCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Francisco Manuel Luque Martínez");
        assertThat(saved.getSlug()).isEqualTo("francisco-manuel-luque-martinez");
        assertThat(saved.getRole()).isEqualTo("Escritor, guionista y colaborador en Reunión de Arte");
        assertThat(saved.getBio()).isEqualTo("Biografia");
        assertThat(saved.getAvatarUrl()).isEqualTo("/authors/francisco-manuel-luque-martinez.jpeg");
        assertThat(saved.getWebsiteUrl()).isEqualTo("https://franciscomluque.wixsite.com/escritor-francisco-m");
        assertThat(saved.getLetterboxdUrl()).isNull();
    }

    @Test
    void updateAuthorPersistsChanges() throws Exception {
        Author existing = author(2L, "María García Santiago", "maria-garcia-santiago", null, null);
        when(authors.findById(2L)).thenReturn(Optional.of(existing));
        when(authors.findBySlug("maria-garcia-santiago")).thenReturn(Optional.of(existing));
        when(authors.saveAndFlush(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admin/authors/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "María García Santiago",
                                  "slug": "maria-garcia-santiago",
                                  "role": "Editora en Reunión de Arte",
                                  "bio": "Nueva biografia",
                                  "avatarUrl": "",
                                  "websiteUrl": "",
                                  "letterboxdUrl": "https://letterboxd.com/mariasantisima/"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Editora en Reunión de Arte"))
                .andExpect(jsonPath("$.letterboxdUrl").value("https://letterboxd.com/mariasantisima/"));

        assertThat(existing.getRole()).isEqualTo("Editora en Reunión de Arte");
        assertThat(existing.getBio()).isEqualTo("Nueva biografia");
        assertThat(existing.getLetterboxdUrl()).isEqualTo("https://letterboxd.com/mariasantisima/");
    }

    @Test
    void createAuthorRejectsDuplicateSlugWithConflict() throws Exception {
        when(authors.findBySlug("jose-luis-olmedo")).thenReturn(Optional.of(
                author(1L, "José Luis Olmedo Barrionuevo", "jose-luis-olmedo", null, null)
        ));

        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Otro autor",
                                  "slug": "jose-luis-olmedo",
                                  "role": "Colaborador",
                                  "bio": "",
                                  "avatarUrl": "",
                                  "websiteUrl": "",
                                  "letterboxdUrl": ""
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Author slug already exists"));
    }

    @Test
    void createAuthorMapsDatabaseDuplicateSlugToConflict() throws Exception {
        when(authors.findBySlug("francisco-manuel-luque-martinez")).thenReturn(Optional.empty());
        when(authors.saveAndFlush(any(Author.class))).thenThrow(new DataIntegrityViolationException("duplicate slug"));

        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Author slug already exists"));
    }

    @Test
    void updateAuthorMapsDatabaseDuplicateSlugToConflict() throws Exception {
        Author existing = author(3L, "Francisco Manuel Luque Martínez", "francisco-manuel-luque-martinez", null, null);
        when(authors.findById(3L)).thenReturn(Optional.of(existing));
        when(authors.findBySlug("francisco-manuel-luque-martinez")).thenReturn(Optional.of(existing));
        when(authors.saveAndFlush(any(Author.class))).thenThrow(new DataIntegrityViolationException("duplicate slug"));

        mockMvc.perform(put("/api/admin/authors/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Author slug already exists"));
    }

    @ParameterizedTest
    @MethodSource("unsafeAuthorUrls")
    void createAuthorRejectsUnsafeAuthorUrls(String field, String value) throws Exception {
        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWith(field, value)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields[0].field").value(field));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/authors/francisco-manuel-luque-martinez.jpeg",
            "https://cdn.example.com/authors/francisco.jpg"
    })
    void createAuthorAcceptsSafeAvatarUrls(String avatarUrl) throws Exception {
        when(authors.findBySlug("francisco-manuel-luque-martinez")).thenReturn(Optional.empty());
        when(authors.saveAndFlush(any(Author.class))).thenAnswer(invocation -> {
            Author saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 3L);
            return saved;
        });

        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWith("avatarUrl", avatarUrl)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.avatarUrl").value(avatarUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://franciscomluque.wixsite.com/escritor-francisco-m",
            "https://letterboxd.com/rdajose/",
            "HTTPS://example.com:8443/path/to/page?one=1&two=dos#bio"
    })
    void createAuthorAcceptsSafeExternalUrls(String url) throws Exception {
        when(authors.findBySlug("francisco-manuel-luque-martinez")).thenReturn(Optional.empty());
        when(authors.saveAndFlush(any(Author.class))).thenAnswer(invocation -> {
            Author saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 3L);
            return saved;
        });

        mockMvc.perform(post("/api/admin/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWith("websiteUrl", url)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.websiteUrl").value(url));
    }

    private Author author(Long id, String name, String slug, String websiteUrl, String letterboxdUrl) {
        Author author = new Author();
        ReflectionTestUtils.setField(author, "id", id);
        author.applyAdminUpdate(name, slug, "Colaborador", "Bio", null, websiteUrl, letterboxdUrl);
        return author;
    }

    private static Stream<Arguments> unsafeAuthorUrls() {
        return Stream.of(
                Arguments.of("avatarUrl", "/authors/../x.jpeg"),
                Arguments.of("avatarUrl", "/authors\\archivo.jpeg"),
                Arguments.of("avatarUrl", "/authors/subcarpeta/archivo.jpeg"),
                Arguments.of("avatarUrl", "/authors/%2e.jpeg"),
                Arguments.of("avatarUrl", "/authors/file" + ((char) 1) + ".jpeg"),
                Arguments.of("websiteUrl", "https://example.com\\path"),
                Arguments.of("websiteUrl", "javascript:alert(1)"),
                Arguments.of("websiteUrl", "data:text/html,hello"),
                Arguments.of("websiteUrl", "https:///path"),
                Arguments.of("websiteUrl", "https://user:pass@example.com/path"),
                Arguments.of("websiteUrl", "https://example.com/<bad>"),
                Arguments.of("websiteUrl", "https://example.com/\"bad\""),
                Arguments.of("websiteUrl", "https://example.com/'bad'"),
                Arguments.of("letterboxdUrl", "https://example.com\\path"),
                Arguments.of("letterboxdUrl", "javascript:alert(1)"),
                Arguments.of("letterboxdUrl", "data:text/html,hello"),
                Arguments.of("letterboxdUrl", "https:///path"),
                Arguments.of("letterboxdUrl", "https://user:pass@example.com/path"),
                Arguments.of("letterboxdUrl", "https://example.com/<bad>")
        );
    }

    private String validPayload() {
        return payloadWith("websiteUrl", "https://franciscomluque.wixsite.com/escritor-francisco-m");
    }

    private String payloadWith(String field, String value) {
        return """
                {
                  "name": "Francisco Manuel Luque Martínez",
                  "slug": "francisco-manuel-luque-martinez",
                  "role": "Colaborador",
                  "bio": "",
                  "avatarUrl": "",
                  "websiteUrl": "",
                  "letterboxdUrl": ""
                }
                """.replace("\"" + field + "\": \"\"", "\"" + field + "\": \"" + jsonEscape(value) + "\"");
    }

    private String jsonEscape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            escaped.append(switch (character) {
                case '\\' -> "\\\\";
                case '"' -> "\\\"";
                case '\b' -> "\\b";
                case '\f' -> "\\f";
                case '\n' -> "\\n";
                case '\r' -> "\\r";
                case '\t' -> "\\t";
                default -> character < 0x20 ? String.format("\\u%04x", (int) character) : character;
            });
        }
        return escaped.toString();
    }

    private LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return validator;
    }
}
