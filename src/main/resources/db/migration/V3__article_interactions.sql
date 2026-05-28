CREATE TABLE article_likes (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  visitor_key_hash VARCHAR(64) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_article_likes_article_visitor UNIQUE (article_id, visitor_key_hash)
);

CREATE INDEX idx_article_likes_article ON article_likes(article_id);

CREATE TABLE article_comments (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  public_name VARCHAR(80) NOT NULL,
  body TEXT NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'PENDING',
  consent_accepted BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  approved_at TIMESTAMPTZ,
  rejected_at TIMESTAMPTZ,
  moderation_notes TEXT,
  CONSTRAINT chk_article_comments_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_article_comments_article_status_created ON article_comments(article_id, status, created_at DESC);
CREATE INDEX idx_article_comments_status_created ON article_comments(status, created_at DESC);
