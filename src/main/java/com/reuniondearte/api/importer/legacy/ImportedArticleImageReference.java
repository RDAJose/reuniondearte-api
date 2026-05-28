package com.reuniondearte.api.importer.legacy;

record ImportedArticleImageReference(
        String field,
        String url,
        String altText,
        String caption,
        String credit
) {
}
