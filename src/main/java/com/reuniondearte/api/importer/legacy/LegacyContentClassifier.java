package com.reuniondearte.api.importer.legacy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Component
public class LegacyContentClassifier {
    private static final int MIN_WORDS = 180;
    private static final Set<String> EXCLUDED_SEGMENTS = Set.of(
            "wp-content", "wp-includes", "wp-admin", "rss", "feed", "atom", "xml",
            "episode", "episodes", "track", "tracks", "podcast", "podcasts",
            "tag", "tags", "category", "author", "search", "menu", "menus",
            "archivo", "archivos", "page", "pagina"
    );
    private static final List<String> TECHNICAL_PATTERNS = List.of(
            "sitemap", "robots", "wp-json", "xmlrpc", "license", "readme",
            "favicon", "manifest", "comments", "attachment", "login", "contacto",
            "privacy", "privacidad", "cookies", "aviso-legal"
    );
    private final LegacyTextCleaner cleaner;

    public LegacyContentClassifier(LegacyTextCleaner cleaner) {
        this.cleaner = cleaner;
    }

    public Optional<String> excludedByPath(Path relativePath) {
        String normalized = relativePath.toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        for (String pattern : TECHNICAL_PATTERNS) {
            if (normalized.contains(pattern)) {
                return Optional.of("technical-page");
            }
        }
        for (Path segment : relativePath) {
            String cleanSegment = segment.toString().toLowerCase(Locale.ROOT);
            if (EXCLUDED_SEGMENTS.contains(cleanSegment)) {
                return Optional.of("excluded-path-" + cleanSegment);
            }
        }
        if (normalized.matches(".*/(20\\d{2}|19\\d{2})/?(index\\.(html?|php))?$")
                || normalized.matches(".*/(20\\d{2}|19\\d{2})/[01]?\\d/?(index\\.(html?|php))?$")) {
            return Optional.of("archive-page");
        }
        return Optional.empty();
    }

    public Optional<String> discardReason(Document document, String text, int wordCount) {
        if (wordCount < MIN_WORDS) {
            return Optional.of("low-text");
        }
        if (looksLikeArchive(document)) {
            return Optional.of("archive-page");
        }
        if (looksLikeSearchOrMenu(document, wordCount)) {
            return Optional.of("menu-or-search-page");
        }
        if (cleaner.title(document).isBlank()) {
            return Optional.of("missing-title");
        }
        if (linkDensity(document, text) > 0.45) {
            return Optional.of("high-link-density");
        }
        return Optional.empty();
    }

    public Element articleRoot(Document document) {
        Element article = document.selectFirst("article.ast-article-single, article[itemtype*=CreativeWork], article, .post, .entry-content, .post-content, .content, main");
        return article == null ? document.body() : article;
    }

    private boolean looksLikeArchive(Document document) {
        String bodyClass = document.body() == null ? "" : document.body().className().toLowerCase(Locale.ROOT);
        String title = cleaner.title(document).toLowerCase(Locale.ROOT);
        int articleCount = document.select("article, .post, .entry, .hentry").size();
        boolean singlePost = bodyClass.contains("single-post")
                || bodyClass.contains("wp-singular")
                || document.selectFirst("article.ast-article-single, article[itemtype*=CreativeWork]") != null;
        return !singlePost && (bodyClass.contains("archive")
                || bodyClass.contains("tag")
                || bodyClass.contains("search")
                || title.startsWith("archivo")
                || title.startsWith("tag:")
                || title.startsWith("buscar")
                || articleCount > 8);
    }

    private boolean looksLikeSearchOrMenu(Document document, int wordCount) {
        int menuLinks = document.select("nav a, .menu a, .navbar a, .navigation a").size();
        int headings = document.select("h1, h2, h3").size();
        return menuLinks > 30 || (headings > 20 && wordCount < 400);
    }

    private double linkDensity(Document document, String text) {
        int textLength = Math.max(text.length(), 1);
        int linkTextLength = articleRoot(document).select("a").stream()
                .map(Element::text)
                .mapToInt(String::length)
                .sum();
        return (double) linkTextLength / textLength;
    }
}
