package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.config.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NewsletterPublicControllerTest {
    private final NewsletterService newsletter = mock(NewsletterService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new NewsletterPublicController(newsletter))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator())
            .build();

    @Test
    void subscribeAcceptsExpectedJson() throws Exception {
        when(newsletter.subscribe(any())).thenReturn(new NewsletterSubscribeResponse("ok"));

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "josele.olmedobarrionuevo@gmail.com",
                                  "consentAccepted": true,
                                  "website": ""
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));

        ArgumentCaptor<NewsletterSubscribeRequest> request = ArgumentCaptor.forClass(NewsletterSubscribeRequest.class);
        verify(newsletter).subscribe(request.capture());
        assertThat(request.getValue().email()).isEqualTo("josele.olmedobarrionuevo@gmail.com");
        assertThat(request.getValue().consentAccepted()).isTrue();
        assertThat(request.getValue().website()).isEmpty();
    }

    @Test
    void subscribeReturnsClearConsentError() throws Exception {
        when(newsletter.subscribe(any())).thenThrow(new ConsentRequiredException());

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "josele.olmedobarrionuevo@gmail.com",
                                  "consentAccepted": false,
                                  "website": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CONSENT_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Consent is required"));
    }

    @Test
    void subscribeReturnsClearEmailServiceError() throws Exception {
        when(newsletter.subscribe(any())).thenThrow(new EmailServiceUnavailableException());

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "josele.olmedobarrionuevo@gmail.com",
                                  "consentAccepted": true,
                                  "website": ""
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("EMAIL_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Email service is not configured or could not send confirmation"));
    }

    @Test
    void subscribeReturnsClearValidationError() throws Exception {
        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "consentAccepted": true,
                                  "website": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Email is invalid"))
                .andExpect(jsonPath("$.fields[0].field").value("email"));
    }

    private LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return validator;
    }
}
