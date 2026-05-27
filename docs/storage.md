# Almacenamiento

## Base de Datos

En desarrollo, PostgreSQL corre en Docker y guarda sus datos en:

```text
C:\Users\sytru\Desktop\reuniondearte-api\storage\postgres-data
```

Los articulos, categorias, autores, SEO, destacados, revisiones, redirects y metadatos de imagenes viven en PostgreSQL.

## Imagenes

La API separa los metadatos editoriales de los archivos fisicos:

- PostgreSQL guarda `media_assets`, `cover_media_id` y los campos legales/editoriales.
- El proveedor de storage guarda el binario de la imagen.

El proveedor se elige con:

```text
RDA_STORAGE_PROVIDER=local|s3
```

## Desarrollo Local

En desarrollo se usa el proveedor `local`:

```text
RDA_STORAGE_PROVIDER=local
RDA_MEDIA_ROOT=./storage/media
RDA_PUBLIC_BASE_URL=http://localhost:8080
```

Los archivos de imagen se guardan en:

```text
C:\Users\sytru\Desktop\reuniondearte-api\storage\media
```

La API sirve esos archivos desde:

```text
/media/**
```

Ejemplo de `public_url` local:

```text
http://localhost:8080/media/articles/slug-del-articulo/cover.png
```

## Produccion Con S3 O Cloudflare R2

En produccion se puede activar el proveedor compatible S3:

```text
RDA_STORAGE_PROVIDER=s3
RDA_S3_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
RDA_S3_REGION=auto
RDA_S3_BUCKET=reuniondearte-media
RDA_S3_ACCESS_KEY=<access-key>
RDA_S3_SECRET_KEY=<secret-key>
RDA_S3_PUBLIC_BASE_URL=https://media.reuniondearte.com
```

Para Cloudflare R2:

- Crea un bucket privado para escrituras desde la API.
- Crea un token/API key con permisos de escritura sobre el bucket.
- Configura un dominio publico o custom domain para servir las imagenes.
- Usa ese dominio como `RDA_S3_PUBLIC_BASE_URL`.

La API subira objetos con claves como:

```text
articles/slug-del-articulo/cover.png
```

Y guardara en PostgreSQL:

```text
storage_provider=s3
storage_path=articles/slug-del-articulo/cover.png
public_url=https://media.reuniondearte.com/articles/slug-del-articulo/cover.png
```

## Metadatos

La tabla `media_assets` guarda los metadatos legales y tecnicos:

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

## Variables Disponibles

```text
RDA_STORAGE_PROVIDER=local|s3
RDA_MEDIA_ROOT=./storage/media
RDA_S3_ENDPOINT=
RDA_S3_REGION=auto
RDA_S3_BUCKET=
RDA_S3_ACCESS_KEY=
RDA_S3_SECRET_KEY=
RDA_S3_PUBLIC_BASE_URL=
```
