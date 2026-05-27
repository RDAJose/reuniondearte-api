# Plan de importación de drafts

La importación de los 55 artículos extraídos queda para una fase posterior. No se ejecuta en esta primera fase.

Origen previsto:

```text
C:\Users\sytru\Desktop\reuniondearte-web-oficial\_local\extracted-drafts
```

Reglas:

- no borrar Markdown;
- no borrar `_local`;
- importar siempre como `draft`;
- preservar slugs;
- guardar `import_source`;
- guardar `import_original_path`;
- generar logs en `storage/import-logs`;
- hacer backup previo de PostgreSQL.

Proceso futuro:

1. Leer cada carpeta con `index.md`.
2. Parsear frontmatter.
3. Reparar problemas de codificación cuando existan.
4. Limpiar navegación o texto residual importado.
5. Crear categorías si faltan.
6. Insertar artículos como `draft`.
7. Asociar imágenes si existen.
8. Generar reporte de importación.

