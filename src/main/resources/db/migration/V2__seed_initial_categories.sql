INSERT INTO categories (name, slug, sort_order) VALUES
  ('Cine', 'cine', 10),
  ('Música', 'musica', 20),
  ('Arte', 'arte', 30),
  ('Libros', 'libros', 40),
  ('Cultura', 'cultura', 50)
ON CONFLICT (slug) DO NOTHING;
