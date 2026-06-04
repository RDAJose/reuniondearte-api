package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.article.ArticleRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class NewsletterServiceTest {
    private final NewsletterSubscriberRepository subscribers = mock(NewsletterSubscriberRepository.class);
    private final NewsletterSendLogRepository sendLogs = mock(NewsletterSendLogRepository.class);
    private final ArticleRepository articles = mock(ArticleRepository.class);
    private final NewsletterTokenService tokens = mock(NewsletterTokenService.class);
    private final NewsletterMailService mail = mock(NewsletterMailService.class);
    private final NewsletterService newsletter = new NewsletterService(subscribers, sendLogs, articles, tokens, mail);

    @Test
    void subscribeHoneypotReturnsNeutralResponseWithoutSavingOrSending() {
        NewsletterSubscribeResponse response = newsletter.subscribe(new NewsletterSubscribeRequest(
                "josele.olmedobarrionuevo@gmail.com",
                true,
                "https://spam.example"
        ));

        assertThat(response.message()).isEqualTo("Si el email puede suscribirse, recibira un correo de confirmacion.");
        verify(subscribers, never()).save(org.mockito.ArgumentMatchers.any());
        verify(mail, never()).sendConfirmation(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
