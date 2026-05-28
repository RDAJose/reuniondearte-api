package com.reuniondearte.api.importer.legacy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

final class ImportedArticleHtmlToMarkdownConverter {
    String convert(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        Document document = Jsoup.parseBodyFragment(html);
        StringBuilder markdown = new StringBuilder();
        for (Node node : document.body().childNodes()) {
            appendBlock(node, markdown, 0);
        }
        return cleanup(markdown.toString());
    }

    private void appendBlock(Node node, StringBuilder markdown, int orderedIndex) {
        if (node instanceof TextNode textNode) {
            appendText(markdown, textNode.text());
            return;
        }
        if (!(node instanceof Element element)) {
            return;
        }

        String tag = element.tagName().toLowerCase();
        switch (tag) {
            case "h2" -> appendParagraph(markdown, "## " + inlineText(element));
            case "h3" -> appendParagraph(markdown, "### " + inlineText(element));
            case "p", "div", "section", "article" -> appendParagraph(markdown, inlineText(element));
            case "blockquote" -> appendParagraph(markdown, "> " + inlineText(element).replace("\n", "\n> "));
            case "ul" -> appendList(element, markdown, false);
            case "ol" -> appendList(element, markdown, true);
            case "img" -> appendParagraph(markdown, imageMarkdown(element));
            case "br" -> markdown.append("\n");
            default -> {
                String text = inlineText(element);
                if (!text.isBlank()) {
                    appendParagraph(markdown, text);
                }
            }
        }
    }

    private void appendList(Element list, StringBuilder markdown, boolean ordered) {
        int index = 1;
        for (Element item : list.children()) {
            if (!"li".equalsIgnoreCase(item.tagName())) {
                continue;
            }
            markdown.append(ordered ? index + ". " : "- ");
            markdown.append(inlineText(item)).append("\n");
            index++;
        }
        markdown.append("\n");
    }

    private String inlineText(Element element) {
        StringBuilder text = new StringBuilder();
        for (Node child : element.childNodes()) {
            appendInline(child, text);
        }
        return text.toString().replaceAll("[ \\t\\x0B\\f\\r]+", " ").trim();
    }

    private void appendInline(Node node, StringBuilder text) {
        if (node instanceof TextNode textNode) {
            appendText(text, textNode.text());
            return;
        }
        if (!(node instanceof Element element)) {
            return;
        }

        String tag = element.tagName().toLowerCase();
        switch (tag) {
            case "strong", "b" -> text.append("**").append(inlineText(element)).append("**");
            case "em", "i" -> text.append("*").append(inlineText(element)).append("*");
            case "a" -> {
                String href = element.attr("abs:href");
                if (href.isBlank()) {
                    href = element.attr("href");
                }
                String label = inlineText(element);
                text.append(href.isBlank() ? label : "[" + label + "](" + href + ")");
            }
            case "img" -> text.append(imageMarkdown(element));
            case "br" -> text.append("\n");
            default -> text.append(inlineText(element));
        }
    }

    private String imageMarkdown(Element element) {
        String src = element.attr("abs:src");
        if (src.isBlank()) {
            src = element.attr("src");
        }
        return "![" + element.attr("alt").trim() + "](" + src.trim() + ")";
    }

    private void appendParagraph(StringBuilder markdown, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        markdown.append(value.trim()).append("\n\n");
    }

    private void appendText(StringBuilder target, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        target.append(value);
    }

    private String cleanup(String value) {
        return value.replaceAll("\\n{3,}", "\n\n").trim() + "\n";
    }
}
