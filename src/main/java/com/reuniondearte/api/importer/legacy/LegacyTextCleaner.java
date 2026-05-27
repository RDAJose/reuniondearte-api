package com.reuniondearte.api.importer.legacy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class LegacyTextCleaner {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final List<String> NOISE_SELECTORS = List.of(
            "script", "style", "noscript", "iframe", "form", "nav", "header", "footer",
            ".menu", ".menus", ".navbar", ".navigation", ".nav", ".sidebar", ".widget",
            ".comments", "#comments", ".comment", ".sharedaddy", ".screen-reader-text",
            ".cookie", ".search", ".pagination", ".page-numbers", ".breadcrumb"
    );

    public String cleanText(Element element) {
        Element copy = element.clone();
        copy.select(String.join(",", NOISE_SELECTORS)).remove();
        return normalize(copy.text());
    }

    public String excerpt(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = normalize(text);
        if (normalized.length() <= 240) {
            return normalized;
        }
        int end = normalized.lastIndexOf(' ', 240);
        return normalized.substring(0, end > 120 ? end : 240).trim();
    }

    public int wordCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (int) Arrays.stream(WHITESPACE.split(text.trim()))
                .filter(word -> word.length() > 1)
                .count();
    }

    public boolean hasPossibleMojibake(String text) {
        return text != null
                && (text.indexOf('\u00c3') >= 0
                || text.indexOf('\u00c2') >= 0
                || text.indexOf('\ufffd') >= 0
                || text.contains("\u00e2\u20ac"));
    }

    public String title(Document document) {
        String headline = firstText(document, "article h1, .post h1, .entry-title, h1");
        if (!headline.isBlank()) {
            return normalize(headline);
        }
        return normalize(document.title());
    }

    public String firstText(Document document, String selector) {
        Element element = document.selectFirst(selector);
        return element == null ? "" : normalize(element.text());
    }

    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        return WHITESPACE.matcher(value.replace('\u00a0', ' ')).replaceAll(" ").trim();
    }
}
