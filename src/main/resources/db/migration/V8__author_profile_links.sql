ALTER TABLE authors ADD COLUMN IF NOT EXISTS website_url TEXT;
ALTER TABLE authors ADD COLUMN IF NOT EXISTS letterboxd_url TEXT;

UPDATE authors
SET letterboxd_url = 'https://letterboxd.com/rdajose/'
WHERE slug = 'jose-luis-olmedo'
  AND (letterboxd_url IS NULL OR letterboxd_url = '');

UPDATE authors
SET letterboxd_url = 'https://letterboxd.com/mariasantisima/'
WHERE slug = 'maria-garcia-santiago'
  AND (letterboxd_url IS NULL OR letterboxd_url = '');
