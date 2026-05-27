# Almacenamiento

## Base de datos

En desarrollo, PostgreSQL corre en Docker y guarda sus datos en:

```text
C:\Users\sytru\Desktop\reuniondearte-api\storage\postgres-data
```

Los artículos, categorías, autores, SEO, destacados, revisiones, redirects y metadatos de imágenes viven en PostgreSQL.

## Imágenes

En desarrollo, los archivos de imagen se guardarán en:

```text
C:\Users\sytru\Desktop\reuniondearte-api\storage\media
```

La tabla `media_assets` guarda los metadatos legales y técnicos:

- `alt_text`
- `caption`
- `credit`
- `source_url`
- `rights_notes`
- `filename`
- `mime_type`
- `size_bytes`
- `width`
- `height`
- `storage_path`
- `public_url`
- `storage_provider`

## Futuro storage

Para producción conviene mover archivos a storage de objetos, por ejemplo Cloudflare R2, S3 o Supabase Storage. La API mantendría el mismo modelo: archivo en storage, metadatos en PostgreSQL.

