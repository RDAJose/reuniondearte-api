# Despliegue En Render

Esta guia prepara el backend Spring Boot para produccion en Render con PostgreSQL gestionado y Cloudflare R2 para imagenes.

## 1. Crear PostgreSQL En Render

1. En Render, crea una base de datos PostgreSQL gestionada.
2. Guarda el `Internal Database URL`.
3. Usalo como `DATABASE_URL` en el servicio web.

La API acepta:

```text
DATABASE_URL=postgres://user:password@host:5432/database
```

Tambien acepta una URL JDBC:

```text
DATABASE_URL=jdbc:postgresql://host:5432/database
```

Si no se usa `DATABASE_URL`, configura:

```text
RDA_DB_HOST=
RDA_DB_PORT=5432
RDA_DB_NAME=
RDA_DB_USER=
RDA_DB_PASSWORD=
```

## 2. Configurar Cloudflare R2

1. Crea un bucket en Cloudflare R2, por ejemplo `reuniondearte-media`.
2. Crea credenciales S3 para la API con permiso de escritura sobre el bucket.
3. Configura un dominio publico o custom domain para servir el bucket.
4. Usa ese dominio como `RDA_S3_PUBLIC_BASE_URL`.

Variables R2:

```text
RDA_STORAGE_PROVIDER=s3
RDA_S3_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
RDA_S3_REGION=auto
RDA_S3_BUCKET=reuniondearte-media
RDA_S3_ACCESS_KEY=<access-key>
RDA_S3_SECRET_KEY=<secret-key>
RDA_S3_PUBLIC_BASE_URL=https://media.reuniondearte.com
RDA_S3_CACHE_CONTROL=public, max-age=31536000
```

No subas claves ni secretos al repositorio.

## 3. Crear El Servicio Web En Render

Puedes crear el servicio manualmente o usar `render.yaml`.

Configuracion recomendada:

```text
Runtime: Docker
Dockerfile: Dockerfile
Health Check Path: /api/health
Environment: production
```

Variables obligatorias:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<Internal Database URL de Render>
RDA_ADMIN_USER=<usuario-admin-no-default>
RDA_ADMIN_PASSWORD=<password-largo-y-unico>
RDA_PUBLIC_BASE_URL=https://<api-render-url>
RDA_ALLOWED_ORIGINS=https://reuniondearte.com,https://www.reuniondearte.com
RDA_STORAGE_PROVIDER=s3
RDA_S3_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
RDA_S3_REGION=auto
RDA_S3_BUCKET=<bucket>
RDA_S3_ACCESS_KEY=<access-key>
RDA_S3_SECRET_KEY=<secret-key>
RDA_S3_PUBLIC_BASE_URL=https://<dominio-publico-media>
RDA_S3_CACHE_CONTROL=public, max-age=31536000
```

Variables newsletter con Brevo Transactional Email API:

```text
RDA_MAIL_PROVIDER=brevo
RDA_BREVO_API_KEY=<brevo-api-key>
RDA_MAIL_FROM=josele.olmedobarrionuevo@gmail.com
RDA_MAIL_FROM_NAME=Reunion de Arte
RDA_MAIL_TIMEOUT_MS=10000
RDA_PUBLIC_SITE_URL=https://reuniondearte.com
RDA_API_PUBLIC_URL=https://reuniondearte-api.onrender.com
```

Render puede bloquear o hacer inviable la salida SMTP a `smtp.gmail.com:587`; por eso la newsletter usa HTTPS contra Brevo (`https://api.brevo.com/v3/smtp/email`) y no Gmail SMTP en produccion. Brevo solo se usa como proveedor de envio transaccional: los suscriptores, estados, tokens hasheados, bajas, logs y export CSV siguen guardandose en PostgreSQL propio.

Las variables SMTP antiguas (`RDA_MAIL_HOST`, `RDA_MAIL_PORT`, `RDA_MAIL_USERNAME`, `RDA_MAIL_PASSWORD`) pueden quedar sin configurar si `RDA_MAIL_PROVIDER=brevo`.

No uses en produccion:

```text
RDA_ADMIN_USER=admin
RDA_ADMIN_PASSWORD=admin_dev_password
```

Con perfil `prod`, la aplicacion falla al arrancar si detecta esas credenciales por defecto.

## 4. Migraciones

Flyway se ejecuta al arrancar:

```text
spring.flyway.enabled=true
```

La base debe quedar validada con las migraciones en:

```text
src/main/resources/db/migration
```

## 5. Backup De PostgreSQL

Desde una maquina con `pg_dump`:

```bash
pg_dump "$DATABASE_URL" --format=custom --file=reuniondearte-prod-$(date +%Y%m%d-%H%M%S).dump
```

Guarda el dump fuera del repositorio, en un lugar privado.

## 6. Restaurar Backup

Para restaurar en una base vacia:

```bash
pg_restore --clean --if-exists --no-owner --dbname "$DATABASE_URL" reuniondearte-prod.dump
```

En Render, usa una ventana de mantenimiento antes de restaurar una base en uso.

## 7. Comprobaciones

Health publico:

```bash
curl https://<api-render-url>/api/health
```

Articulos publicados:

```bash
curl https://<api-render-url>/api/articles
```

Admin protegido:

```bash
curl -i https://<api-render-url>/admin
curl -u "$RDA_ADMIN_USER:$RDA_ADMIN_PASSWORD" https://<api-render-url>/admin
```

Subida de imagen:

```bash
curl -u "$RDA_ADMIN_USER:$RDA_ADMIN_PASSWORD" \
  -F "file=@cover.png;type=image/png" \
  -F "altText=Descripcion editorial de la imagen" \
  -F "caption=Pie de foto" \
  -F "credit=Credito" \
  https://<api-render-url>/api/admin/articles/<id>/cover
```

Comprueba que `media_assets.storage_provider` queda como `s3` y que `public_url` apunta al dominio publico de R2.

Comprueba tambien las cabeceras publicas de una imagen nueva:

```bash
curl -I https://<dominio-publico-media>/articles/<slug>/cover.png
```

La respuesta debe incluir `Content-Type` correcto, `Cache-Control: public, max-age=31536000` o el valor configurado en `RDA_S3_CACHE_CONTROL`, y un `Content-Length` acorde al peso esperado. No configures `immutable` mientras se reutilicen URLs como `cover.png`; usalo solo si cada cambio genera una ruta unica/versionada.
