package com.reuniondearte.api.newsletter;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {
    Optional<NewsletterSubscriber> findByEmailNormalized(String emailNormalized);

    Optional<NewsletterSubscriber> findByConfirmationTokenHash(String confirmationTokenHash);

    Optional<NewsletterSubscriber> findByUnsubscribeTokenHash(String unsubscribeTokenHash);

    List<NewsletterSubscriber> findByStatusOrderByCreatedAtDesc(NewsletterSubscriberStatus status, Pageable pageable);

    List<NewsletterSubscriber> findByStatusOrderByCreatedAtAsc(NewsletterSubscriberStatus status);

    long countByStatus(NewsletterSubscriberStatus status);
}
