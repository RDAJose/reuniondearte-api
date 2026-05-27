package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.ArticleStatus;
import jakarta.validation.constraints.NotNull;

public record AdminArticleStatusRequest(@NotNull ArticleStatus status) {
}
