package com.reuniondearte.api.newsletter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterSendLogRepository extends JpaRepository<NewsletterSendLog, Long> {
}
