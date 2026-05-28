package com.reuniondearte.api.article;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

final class ArticleContentRenderer {
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    private ArticleContentRenderer() {
    }

    static String htmlFrom(Article article) {
        String contentHtml = article.getContentHtml();
        if (contentHtml != null && !contentHtml.isBlank()) {
            return contentHtml;
        }

        String contentMarkdown = article.getContentMarkdown();
        if (contentMarkdown == null || contentMarkdown.isBlank()) {
            return contentHtml;
        }

        return HTML_RENDERER.render(PARSER.parse(contentMarkdown));
    }
}
