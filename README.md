# Reunión de Arte API

Backend editorial aislado para Reunión de Arte.

Este proyecto es independiente de la web pública Next.js. En esta fase no conecta, modifica ni despliega la web oficial.

## Stack

- Java 21
- Spring Boot
- Maven Wrapper
- PostgreSQL
- Flyway
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security

## Arranque local

```powershell
cd C:\Users\sytru\Desktop\reuniondearte-api
docker compose up -d postgres
.\mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

PostgreSQL se publica en el puerto local `55432` para evitar conflictos con instalaciones locales que usen `5432`.

## Endpoints públicos

```text
GET /api/categories
GET /api/articles
GET /api/articles/{slug}
GET /api/categories/{slug}/articles
GET /api/featured
```

Los endpoints públicos solo devuelven artículos con estado `published`.

## Admin

La ruta `/api/admin/**` queda reservada y protegida desde el principio. En esta fase no hay admin abierto ni endpoints de escritura públicos.

## Dónde se guarda todo

```text
Artículos: PostgreSQL
Base de datos local: C:\Users\sytru\Desktop\reuniondearte-api\storage\postgres-data
Imágenes en desarrollo: C:\Users\sytru\Desktop\reuniondearte-api\storage\media
Metadatos de imágenes: PostgreSQL, tabla media_assets
Backups: C:\Users\sytru\Desktop\reuniondearte-api\storage\backups\postgres
Logs de importación: C:\Users\sytru\Desktop\reuniondearte-api\storage\import-logs
```

## Variables

Copia `.env.example` si quieres documentar valores locales. Spring lee las variables de entorno del sistema; Docker Compose ya define PostgreSQL para desarrollo.

## Documentación

- `docs/storage.md`
- `docs/backups.md`
- `docs/import-drafts-plan.md`
- `docs/api.md`
