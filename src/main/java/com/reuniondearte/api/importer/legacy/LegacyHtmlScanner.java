package com.reuniondearte.api.importer.legacy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class LegacyHtmlScanner {
    private static final Pattern DATE_IN_PATH = Pattern.compile(".*[/\\\\]((?:19|20)\\d{2})[/\\\\]([01]?\\d)[/\\\\]([0-3]?\\d)[/\\\\].*");
    private final LegacyContentClassifier classifier;
    private final LegacyTextCleaner cleaner;

    public LegacyHtmlScanner(LegacyContentClassifier classifier, LegacyTextCleaner cleaner) {
        this.classifier = classifier;
        this.cleaner = cleaner;
    }

    public LegacyScanReport scan(Path legacyRoot) throws IOException {
        List<Path> htmlFiles;
        try (Stream<Path> stream = Files.walk(legacyRoot)) {
            htmlFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(this::isHtmlFile)
                    .sorted()
                    .toList();
        }

        List<LegacyArticleCandidate> candidates = new ArrayList<>();
        List<LegacyScanReport.LegacyDiscardedPage> discarded = new ArrayList<>();
        Map<String, Integer> discardedByReason = new LinkedHashMap<>();
        int analyzed = 0;

        for (Path file : htmlFiles) {
            Path relative = legacyRoot.relativize(file);
            var pathExclusion = classifier.excludedByPath(relative);
            if (pathExclusion.isPresent()) {
                addDiscard(discarded, discardedByReason, relative, pathExclusion.get(), "Excluded by path pattern");
                continue;
            }

            analyzed++;
            try {
                Document document = Jsoup.parse(file.toFile(), null, file.toUri().toString());
                Element root = classifier.articleRoot(document);
                String text = cleaner.cleanText(root);
                int wordCount = cleaner.wordCount(text);
                var discardReason = classifier.discardReason(document, text, wordCount);
                if (discardReason.isPresent()) {
                    addDiscard(discarded, discardedByReason, relative, discardReason.get(), "words=" + wordCount);
                    continue;
                }
                candidates.add(candidate(document, root, relative, text, wordCount));
            } catch (IOException | IllegalArgumentException exception) {
                addDiscard(discarded, discardedByReason, relative, "parse-error", exception.getMessage());
            }
        }

        int withImages = (int) candidates.stream().filter(candidate -> !candidate.images().isEmpty()).count();
        int withMojibake = (int) candidates.stream()
                .filter(candidate -> candidate.warnings().contains("possible-mojibake"))
                .count();

        return new LegacyScanReport(
                Instant.now(),
                legacyRoot.toAbsolutePath().normalize().toString(),
                analyzed,
                candidates.size(),
                withImages,
                withMojibake,
                discardedByReason,
                candidates,
                discarded
        );
    }

    private LegacyArticleCandidate candidate(Document document, Element root, Path relative, String text, int wordCount) {
        String title = cleaner.title(document);
        List<String> warnings = new ArrayList<>();
        if (cleaner.hasPossibleMojibake(title) || cleaner.hasPossibleMojibake(text)) {
            warnings.add("possible-mojibake");
        }
        if (document.selectFirst("time, .date, .posted-on, .entry-date") == null && dateFromPath(relative).isBlank()) {
            warnings.add("missing-date");
        }

        return new LegacyArticleCandidate(
                title,
                slug(relative),
                firstNonBlank(dateFromDocument(document), dateFromPath(relative)),
                category(document, relative),
                cleaner.excerpt(text),
                text,
                wordCount,
                links(root),
                images(root),
                relative.toString().replace('\\', '/'),
                warnings
        );
    }

    private boolean isHtmlFile(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".html") || name.endsWith(".htm") || name.endsWith(".php");
    }

    private void addDiscard(List<LegacyScanReport.LegacyDiscardedPage> discarded,
                            Map<String, Integer> discardedByReason,
                            Path relative,
                            String reason,
                            String detail) {
        discarded.add(new LegacyScanReport.LegacyDiscardedPage(
                relative.toString().replace('\\', '/'),
                reason,
                detail == null ? "" : detail
        ));
        discardedByReason.merge(reason, 1, Integer::sum);
    }

    private String dateFromDocument(Document document) {
        Element time = document.selectFirst("time[datetime], meta[property=article:published_time], meta[name=date], .entry-date, .posted-on");
        if (time == null) {
            return "";
        }
        String value = firstNonBlank(time.attr("datetime"), time.attr("content"), time.text());
        return cleaner.normalize(value);
    }

    private String dateFromPath(Path relative) {
        Matcher matcher = DATE_IN_PATH.matcher(relative.toString());
        if (!matcher.matches()) {
            return "";
        }
        return "%s-%02d-%02d".formatted(
                matcher.group(1),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3))
        );
    }

    private String category(Document document, Path relative) {
        String category = cleaner.firstText(document, ".cat-links a, a[rel=category], .category a, .post-categories a");
        if (!category.isBlank()) {
            return category;
        }
        List<String> parts = new ArrayList<>();
        for (Path segment : relative) {
            parts.add(segment.toString());
        }
        if (parts.size() > 1 && !parts.get(0).matches("(19|20)\\d{2}")) {
            return parts.get(0);
        }
        return "";
    }

    private List<LegacyArticleCandidate.LegacyLink> links(Element root) {
        return root.select("a[href]").stream()
                .map(link -> new LegacyArticleCandidate.LegacyLink(cleaner.normalize(link.text()), link.absUrl("href").isBlank() ? link.attr("href") : link.absUrl("href")))
                .filter(link -> !link.href().isBlank())
                .distinct()
                .limit(100)
                .toList();
    }

    private List<LegacyArticleCandidate.LegacyImage> images(Element root) {
        return root.select("img[src]").stream()
                .map(image -> new LegacyArticleCandidate.LegacyImage(
                        image.absUrl("src").isBlank() ? image.attr("src") : image.absUrl("src"),
                        cleaner.normalize(image.attr("alt")),
                        captionFor(image)
                ))
                .filter(image -> !image.src().isBlank())
                .distinct()
                .limit(100)
                .toList();
    }

    private String captionFor(Element image) {
        Element figure = image.closest("figure, .wp-caption, .caption");
        if (figure == null) {
            return "";
        }
        Element caption = figure.selectFirst("figcaption, .wp-caption-text, .caption");
        return caption == null ? "" : cleaner.normalize(caption.text());
    }

    private String slug(Path relative) {
        List<String> parts = new ArrayList<>();
        for (Path segment : relative) {
            parts.add(segment.toString());
        }
        String last = parts.isEmpty() ? relative.toString() : parts.get(parts.size() - 1);
        String withoutExtension = last.replaceFirst("\\.(html?|php)$", "");
        if (withoutExtension.equalsIgnoreCase("index") && parts.size() > 1) {
            return parts.get(parts.size() - 2);
        }
        return withoutExtension;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
