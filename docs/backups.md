# Backups PostgreSQL

Los backups locales se guardan en:

```text
C:\Users\sytru\Desktop\reuniondearte-api\storage\backups\postgres
```

## Crear backup

```powershell
cd C:\Users\sytru\Desktop\reuniondearte-api
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
docker exec reuniondearte-postgres pg_dump -U reuniondearte -d reuniondearte --format=custom --file="/tmp/reuniondearte-$stamp.backup"
docker cp "reuniondearte-postgres:/tmp/reuniondearte-$stamp.backup" ".\storage\backups\postgres\reuniondearte-$stamp.backup"
```

## Restaurar backup

```powershell
cd C:\Users\sytru\Desktop\reuniondearte-api
docker cp ".\storage\backups\postgres\reuniondearte.backup" "reuniondearte-postgres:/tmp/reuniondearte.backup"
docker exec reuniondearte-postgres pg_restore -U reuniondearte -d reuniondearte --clean --if-exists "/tmp/reuniondearte.backup"
```

## Regla operativa

Haz backup antes de:

- importar drafts;
- ejecutar migraciones delicadas;
- publicar una tanda grande de contenido;
- cambiar lógica de SEO, slugs o redirects.

