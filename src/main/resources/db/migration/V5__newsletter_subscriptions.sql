CREATE TABLE newsletter_subscribers (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(320) NOT NULL,
  email_normalized VARCHAR(320) NOT NULL UNIQUE,
  status VARCHAR(40) NOT NULL,
  consent_text TEXT NOT NULL,
  consent_version VARCHAR(50) NOT NULL,
  confirmation_token_hash VARCHAR(128),
  unsubscribe_token_hash VARCHAR(128) NOT NULL,
  confirmed_at TIMESTAMPTZ,
  unsubscribed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_email_sent_at TIMESTAMPTZ,
  source VARCHAR(80),
  notes TEXT,
  CONSTRAINT chk_newsletter_subscribers_status CHECK (status IN ('PENDING_CONFIRMATION', 'ACTIVE', 'UNSUBSCRIBED', 'BOUNCED'))
);

CREATE INDEX idx_newsletter_subscribers_status ON newsletter_subscribers(status);
CREATE INDEX idx_newsletter_subscribers_created_at ON newsletter_subscribers(created_at DESC);

CREATE TABLE newsletter_send_logs (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT REFERENCES articles(id),
  subject VARCHAR(255) NOT NULL,
  recipient_email_hash VARCHAR(128) NOT NULL,
  status VARCHAR(40) NOT NULL,
  error_message TEXT,
  sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_newsletter_send_logs_status CHECK (status IN ('SENT', 'FAILED', 'SKIPPED'))
);

CREATE INDEX idx_newsletter_send_logs_article ON newsletter_send_logs(article_id);
CREATE INDEX idx_newsletter_send_logs_sent_at ON newsletter_send_logs(sent_at DESC);
