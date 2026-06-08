CREATE TABLE IF NOT EXISTS article_authors (
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
  position INT NOT NULL DEFAULT 0,
  PRIMARY KEY (article_id, author_id)
);

CREATE INDEX IF NOT EXISTS idx_article_authors_article ON article_authors(article_id, position);
CREATE INDEX IF NOT EXISTS idx_article_authors_author ON article_authors(author_id);

INSERT INTO article_authors (article_id, author_id, position)
SELECT id, author_id, 0
FROM articles
WHERE author_id IS NOT NULL
ON CONFLICT (article_id, author_id) DO NOTHING;
