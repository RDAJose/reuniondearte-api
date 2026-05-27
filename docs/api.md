# API

## Público

### `GET /api/categories`

Devuelve las categorías editoriales.

### `GET /api/articles`

Devuelve artículos publicados ordenados por fecha de publicación descendente.

### `GET /api/articles/{slug}`

Devuelve un artículo publicado por slug. Si el artículo no existe o no está publicado, devuelve `404`.

### `GET /api/categories/{slug}/articles`

Devuelve artículos publicados de una categoría.

### `GET /api/featured`

Devuelve artículos publicados marcados como destacados y dentro de su ventana temporal.

## Admin

La ruta `/api/admin/**` está protegida. En esta fase solo existe un endpoint de reserva:

```text
GET /api/admin/status
```

Sin credenciales devuelve `401` o `403`.

## SEO

El modelo incluye:

- slugs preservables en `articles.slug`;
- `articles.canonical_url`;
- `seo_metadata.meta_title`;
- `seo_metadata.meta_description`;
- `seo_metadata.noindex`;
- `redirects` para futuras URLs antiguas.

