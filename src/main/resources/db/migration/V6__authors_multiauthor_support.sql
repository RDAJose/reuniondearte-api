ALTER TABLE authors ADD COLUMN IF NOT EXISTS role VARCHAR(260);
ALTER TABLE authors ADD COLUMN IF NOT EXISTS avatar_url TEXT;

INSERT INTO authors (name, slug, role, bio, avatar_url)
VALUES (
  'José Luis Olmedo Barrionuevo',
  'jose-luis-olmedo',
  'Creador, desarrollador y editor de Reunión de Arte',
  'Creador, desarrollador y editor de Reunión de Arte.',
  NULL
)
ON CONFLICT (slug) DO UPDATE SET
  name = EXCLUDED.name,
  role = EXCLUDED.role,
  bio = COALESCE(NULLIF(authors.bio, ''), EXCLUDED.bio),
  avatar_url = COALESCE(authors.avatar_url, EXCLUDED.avatar_url),
  updated_at = now();

INSERT INTO authors (name, slug, role, bio, avatar_url)
VALUES (
  'María García Santiago',
  'maria-garcia-santiago',
  'Pintora, amante del arte y el cine, y editora en Reunión de Arte',
  'Pintora, amante del arte y el cine, y editora en Reunión de Arte.',
  NULL
)
ON CONFLICT (slug) DO UPDATE SET
  name = EXCLUDED.name,
  role = EXCLUDED.role,
  bio = EXCLUDED.bio,
  avatar_url = COALESCE(authors.avatar_url, EXCLUDED.avatar_url),
  updated_at = now();

UPDATE authors
SET role = COALESCE(NULLIF(role, ''), 'Editor/a de Reunión de Arte')
WHERE role IS NULL OR role = '';

ALTER TABLE authors ALTER COLUMN role SET NOT NULL;

UPDATE articles
SET author_id = (SELECT id FROM authors WHERE slug = 'jose-luis-olmedo')
WHERE author_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_articles_author ON articles(author_id);
