CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255),
  display_name VARCHAR(160) NOT NULL,
  role VARCHAR(40) NOT NULL DEFAULT 'editor',
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE media_assets (
  id BIGSERIAL PRIMARY KEY,
  media_type VARCHAR(40) NOT NULL,
  storage_provider VARCHAR(40) NOT NULL DEFAULT 'local',
  storage_path TEXT NOT NULL,
  public_url TEXT,
  filename VARCHAR(255) NOT NULL,
  mime_type VARCHAR(120),
  size_bytes BIGINT,
  width INT,
  height INT,
  alt_text TEXT,
  caption TEXT,
  credit TEXT,
  source_url TEXT,
  rights_notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE authors (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(180) NOT NULL,
  slug VARCHAR(220) NOT NULL UNIQUE,
  bio TEXT,
  avatar_media_id BIGINT REFERENCES media_assets(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE categories (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(140) NOT NULL UNIQUE,
  description TEXT,
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tags (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(140) NOT NULL UNIQUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE articles (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(260) NOT NULL,
  slug VARCHAR(280) NOT NULL UNIQUE,
  excerpt TEXT,
  content_markdown TEXT,
  content_html TEXT,
  status VARCHAR(40) NOT NULL DEFAULT 'draft',
  author_id BIGINT REFERENCES authors(id),
  primary_category_id BIGINT REFERENCES categories(id),
  cover_media_id BIGINT REFERENCES media_assets(id),
  published_at TIMESTAMPTZ,
  scheduled_at TIMESTAMPTZ,
  import_source VARCHAR(120),
  import_original_path TEXT,
  canonical_url TEXT,
  reading_time_minutes INT,
  language VARCHAR(10) NOT NULL DEFAULT 'es',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_articles_status CHECK (status IN ('draft', 'review', 'scheduled', 'published', 'archived'))
);

CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_slug ON articles(slug);
CREATE INDEX idx_articles_published_at ON articles(published_at DESC);
CREATE INDEX idx_articles_primary_category ON articles(primary_category_id);

CREATE TABLE article_tags (
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY (article_id, tag_id)
);

CREATE TABLE article_media (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  media_asset_id BIGINT NOT NULL REFERENCES media_assets(id),
  role VARCHAR(40) NOT NULL DEFAULT 'inline',
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_article_media_article ON article_media(article_id);

CREATE TABLE featured_slots (
  id BIGSERIAL PRIMARY KEY,
  slot_key VARCHAR(120) NOT NULL,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  category_id BIGINT REFERENCES categories(id),
  sort_order INT NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  starts_at TIMESTAMPTZ,
  ends_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_featured_slots_lookup ON featured_slots(active, slot_key, sort_order);

CREATE TABLE seo_metadata (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL UNIQUE REFERENCES articles(id) ON DELETE CASCADE,
  meta_title VARCHAR(280),
  meta_description VARCHAR(500),
  og_title VARCHAR(280),
  og_description VARCHAR(500),
  og_image_media_id BIGINT REFERENCES media_assets(id),
  twitter_title VARCHAR(280),
  twitter_description VARCHAR(500),
  twitter_image_media_id BIGINT REFERENCES media_assets(id),
  canonical_url TEXT,
  noindex BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE article_revisions (
  id BIGSERIAL PRIMARY KEY,
  article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
  user_id BIGINT REFERENCES users(id),
  title VARCHAR(260) NOT NULL,
  content_markdown TEXT,
  content_html TEXT,
  status VARCHAR(40),
  change_note TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_article_revisions_article ON article_revisions(article_id, created_at DESC);

CREATE TABLE redirects (
  id BIGSERIAL PRIMARY KEY,
  source_path VARCHAR(500) NOT NULL UNIQUE,
  target_path VARCHAR(500) NOT NULL,
  status_code INT NOT NULL DEFAULT 301,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  note TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_redirects_status_code CHECK (status_code IN (301, 302, 307, 308))
);

CREATE INDEX idx_redirects_active_source ON redirects(active, source_path);

