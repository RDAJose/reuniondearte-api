package com.reuniondearte.api.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUiController {
    @GetMapping(value = "/admin", produces = MediaType.TEXT_HTML_VALUE)
    public String admin() {
        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Admin editorial - Reunion de Arte</title>
                  <style>
                    :root { color-scheme: light; --ink:#1c1917; --muted:#78716c; --line:#d6d3d1; --paper:#fffdf8; --soft:#f5f2ea; --accent:#334155; }
                    * { box-sizing: border-box; }
                    body { margin: 0; background: var(--paper); color: var(--ink); font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
                    header { border-bottom: 1px solid var(--line); background: #ffffff; padding: 14px 18px; position: sticky; top: 0; z-index: 5; }
                    header h1 { margin: 0; font-size: 18px; letter-spacing: .02em; }
                    main { display: grid; grid-template-columns: 340px minmax(0, 1fr); min-height: calc(100vh - 54px); }
                    aside { border-right: 1px solid var(--line); background: var(--soft); padding: 16px; }
                    section { padding: 18px; }
                    button, input, select, textarea { font: inherit; }
                    button { border: 1px solid var(--ink); background: var(--ink); color: #fff; padding: 9px 12px; cursor: pointer; }
                    button.secondary { background: #fff; color: var(--ink); border-color: var(--line); }
                    button.danger { background: #7f1d1d; border-color: #7f1d1d; }
                    button:disabled { opacity: .55; cursor: not-allowed; }
                    label { display: block; margin-top: 12px; font-size: 12px; font-weight: 700; text-transform: uppercase; color: var(--muted); }
                    input, select, textarea { width: 100%; margin-top: 5px; border: 1px solid var(--line); background: #fff; color: var(--ink); padding: 9px 10px; }
                    textarea { min-height: 160px; resize: vertical; line-height: 1.45; }
                    .toolbar { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-bottom: 14px; }
                    .status-tabs { display: grid; grid-template-columns: repeat(3, 1fr); gap: 6px; margin-bottom: 12px; }
                    .status-tabs button { padding: 8px 6px; font-size: 13px; }
                    .status-tabs button.active { background: var(--accent); border-color: var(--accent); }
                    .article-list { display: flex; flex-direction: column; gap: 8px; }
                    .article-row { display: block; width: 100%; text-align: left; background: #fff; color: var(--ink); border: 1px solid var(--line); padding: 10px; }
                    .article-row.active { border-color: var(--ink); box-shadow: inset 3px 0 0 var(--ink); }
                    .article-row strong { display: block; font-size: 14px; line-height: 1.25; }
                    .article-row span { display: block; margin-top: 5px; color: var(--muted); font-size: 12px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
                    .grid { display: grid; grid-template-columns: minmax(0, 1fr) minmax(320px, 420px); gap: 18px; align-items: start; }
                    .panel { border: 1px solid var(--line); background: #fff; padding: 14px; }
                    .panel h2, .panel h3 { margin: 0 0 8px; font-size: 16px; }
                    .editor-sidebar { position: sticky; top: 70px; max-height: calc(100vh - 88px); overflow-y: auto; display: grid; gap: 10px; padding: 10px; }
                    .side-section { border: 1px solid var(--line); background: #fff; }
                    .side-section summary { cursor: pointer; padding: 10px 12px; background: var(--soft); font-size: 14px; font-weight: 800; }
                    .side-section[open] summary { border-bottom: 1px solid var(--line); }
                    .side-section-body { padding: 12px; }
                    .meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
                    .message { min-height: 24px; margin: 8px 0 12px; color: var(--muted); font-size: 14px; }
                    .public-link { word-break: break-all; color: #1d4ed8; font-size: 14px; }
                    .image-status { display: inline-flex; margin: 4px 0 12px; border: 1px solid var(--line); background: var(--soft); padding: 6px 8px; font-size: 13px; font-weight: 700; }
                    .image-status.assigned { border-color: #166534; color: #166534; background: #f0fdf4; }
                    .image-preview { width: 100%; max-height: 220px; object-fit: cover; border: 1px solid var(--line); background: var(--soft); }
                    .cover-preview { display: block; height: clamp(220px, 34vw, 420px); max-height: none; object-fit: contain; background: #e7e5e4; }
                    .data-list { display: grid; gap: 8px; margin: 8px 0 14px; font-size: 13px; }
                    .data-list div { display: grid; gap: 3px; }
                    .data-list dt { color: var(--muted); font-weight: 700; text-transform: uppercase; font-size: 11px; }
                    .data-list dd { margin: 0; word-break: break-word; }
                    .notice { border-left: 3px solid #92400e; background: #fffbeb; color: #713f12; padding: 10px 12px; font-size: 13px; line-height: 1.45; }
                    .warning-list { display: grid; gap: 6px; margin: 10px 0 12px; padding: 10px 12px; border: 1px solid #f59e0b; background: #fffbeb; color: #713f12; font-size: 13px; line-height: 1.4; }
                    .hint { color: var(--muted); font-size: 12px; line-height: 1.45; }
                    .snippet { width: 100%; min-height: 86px; font-family: ui-monospace, SFMono-Regular, Consolas, "Liberation Mono", monospace; font-size: 12px; }
                    #bodyImages { display: grid; gap: 10px; max-height: min(54vh, 620px); overflow-y: auto; padding-right: 2px; }
                    #mediaFiles { display: grid; gap: 10px; max-height: min(42vh, 460px); overflow-y: auto; padding-right: 2px; }
                    .body-image { border: 1px solid var(--line); margin: 0; padding: 10px; background: #fff; display: grid; grid-template-columns: 92px minmax(0, 1fr); gap: 10px; align-items: start; }
                    .body-thumbnail { width: 92px; height: 92px; max-height: none; object-fit: contain; background: #e7e5e4; }
                    .asset-card-body { min-width: 0; }
                    .asset-title { margin: 0 0 4px; font-size: 13px; line-height: 1.25; }
                    .asset-summary { color: var(--muted); font-size: 12px; line-height: 1.35; margin-bottom: 6px; overflow-wrap: anywhere; }
                    .compact-snippet { min-height: 52px; max-height: 72px; font-size: 11px; line-height: 1.35; }
                    .compact-actions { margin: 8px 0 0; gap: 6px; }
                    .compact-actions button { padding: 7px 8px; font-size: 12px; }
                    .asset-legal { margin-top: 8px; font-size: 12px; }
                    .asset-legal summary { cursor: pointer; color: var(--muted); font-weight: 800; }
                    .asset-legal .data-list { margin-bottom: 0; font-size: 12px; }
                    .body-metadata { border: 1px solid var(--line); background: var(--soft); padding: 10px; margin-top: 10px; }
                    .media-file { border: 1px solid var(--line); margin: 0; padding: 10px; background: #fff; }
                    .media-metadata { border: 1px solid var(--line); background: var(--soft); padding: 10px; margin-top: 10px; }
                    .comment-panel { margin-bottom: 18px; }
                    .comment-list { display: grid; gap: 10px; }
                    .comment-card { border: 1px solid var(--line); background: #fff; padding: 12px; }
                    .comment-card header { position: static; border: 0; padding: 0; background: transparent; }
                    .comment-card strong { display: block; font-size: 14px; }
                    .comment-card small { display: block; margin-top: 3px; color: var(--muted); }
                    .comment-card p { white-space: pre-wrap; line-height: 1.45; margin: 10px 0; }
                    .stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin: 10px 0 12px; }
                    .stat { border: 1px solid var(--line); background: var(--soft); padding: 10px; }
                    .stat strong { display: block; font-size: 20px; }
                    .stat span { display: block; color: var(--muted); font-size: 12px; text-transform: uppercase; font-weight: 700; }
                    .subscriber-list { display: grid; gap: 6px; max-height: 260px; overflow: auto; }
                    .subscriber-row { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 8px; border: 1px solid var(--line); background: #fff; padding: 8px; font-size: 13px; }
                    .subscriber-row span { color: var(--muted); font-size: 12px; }
                    .author-manager { margin-bottom: 18px; }
                    .author-layout { display: grid; grid-template-columns: minmax(220px, 340px) minmax(0, 1fr); gap: 14px; align-items: start; }
                    .author-list { display: grid; gap: 6px; max-height: 360px; overflow: auto; }
                    .author-row { display: block; width: 100%; text-align: left; background: #fff; color: var(--ink); border: 1px solid var(--line); padding: 9px; }
                    .author-row.active { border-color: var(--ink); box-shadow: inset 3px 0 0 var(--ink); }
                    .author-row strong { display: block; font-size: 13px; line-height: 1.25; }
                    .author-row span { display: block; margin-top: 4px; color: var(--muted); font-size: 12px; overflow-wrap: anywhere; }
                    .empty { color: var(--muted); padding: 20px 0; }
                    @media (max-width: 880px) {
                      main { grid-template-columns: 1fr; }
                      aside { border-right: 0; border-bottom: 1px solid var(--line); }
                      .grid { grid-template-columns: 1fr; }
                      .author-layout { grid-template-columns: 1fr; }
                      .editor-sidebar { position: static; max-height: none; overflow: visible; }
                      #bodyImages, #mediaFiles { max-height: none; overflow: visible; }
                      .body-image { grid-template-columns: 76px minmax(0, 1fr); }
                      .body-thumbnail { width: 76px; height: 76px; }
                    }
                  </style>
                </head>
                <body>
                  <header><h1>Admin editorial - Reunion de Arte</h1></header>
                  <main>
                    <aside>
                      <div class="status-tabs">
                        <button type="button" data-status="draft" class="active">Draft</button>
                        <button type="button" data-status="review">Review</button>
                        <button type="button" data-status="published">Published</button>
                      </div>
                      <div class="toolbar">
                        <button type="button" id="refreshButton" class="secondary">Recargar</button>
                        <a href="/admin/tools/editor-bloques" target="_blank" rel="noreferrer" class="public-link" style="font-weight:700;">Editor visual de bloques</a>
                      </div>
                      <div id="articleList" class="article-list"></div>
                    </aside>
                    <section>
                      <p id="message" class="message">Selecciona un articulo.</p>
                      <div class="panel comment-panel">
                        <div class="toolbar">
                          <h2 style="margin:0; font-size:16px; flex:1;">Newsletter / Suscriptores</h2>
                          <select id="newsletterStatus" style="width:auto; margin:0;">
                            <option value="ACTIVE">Activos</option>
                            <option value="PENDING_CONFIRMATION">Pendientes</option>
                            <option value="UNSUBSCRIBED">Bajas</option>
                          </select>
                          <button type="button" id="refreshNewsletterButton" class="secondary">Recargar</button>
                          <a href="/api/admin/newsletter/export.csv" class="public-link" style="font-weight:700;">Exportar CSV</a>
                        </div>
                        <div id="newsletterStats" class="stats"></div>
                        <div id="newsletterSubscribers" class="subscriber-list"></div>
                      </div>
                      <div class="panel comment-panel">
                        <div class="toolbar">
                          <h2 style="margin:0; font-size:16px; flex:1;">Comentarios pendientes</h2>
                          <button type="button" id="refreshCommentsButton" class="secondary">Recargar</button>
                        </div>
                        <div id="pendingComments" class="comment-list"></div>
                      </div>
                      <div class="panel author-manager">
                        <div class="toolbar">
                          <h2 style="margin:0; font-size:16px; flex:1;">Autores</h2>
                          <button type="button" id="newAuthorButton" class="secondary">Nuevo autor</button>
                          <button type="button" id="refreshAuthorsButton" class="secondary">Recargar</button>
                        </div>
                        <div class="author-layout">
                          <div id="authorList" class="author-list"></div>
                          <form id="authorForm">
                            <h3 id="authorFormTitle">Nuevo autor</h3>
                            <div class="meta-grid">
                              <div>
                                <label for="authorName">Nombre</label>
                                <input id="authorName" name="name" required maxlength="180">
                              </div>
                              <div>
                                <label for="authorSlug">Slug</label>
                                <input id="authorSlug" name="slug" required maxlength="220" pattern="[a-z0-9]+(-[a-z0-9]+)+">
                              </div>
                            </div>
                            <label for="authorRole">Funcion</label>
                            <input id="authorRole" name="role" required maxlength="260">
                            <label for="authorBio">Biografia</label>
                            <textarea id="authorBio" name="bio"></textarea>
                            <div class="meta-grid">
                              <div>
                                <label for="authorAvatarUrl">Avatar URL</label>
                                <input id="authorAvatarUrl" name="avatarUrl" placeholder="/authors/nombre.jpeg">
                              </div>
                              <div>
                                <label for="authorWebsiteUrl">Web oficial</label>
                                <input id="authorWebsiteUrl" name="websiteUrl" type="url" placeholder="https://...">
                              </div>
                            </div>
                            <label for="authorLetterboxdUrl">Letterboxd</label>
                            <input id="authorLetterboxdUrl" name="letterboxdUrl" type="url" placeholder="https://letterboxd.com/...">
                            <div class="toolbar" style="margin-top:12px; margin-bottom:0;">
                              <button type="submit" id="saveAuthorButton">Crear autor</button>
                              <button type="button" id="clearAuthorButton" class="secondary">Limpiar</button>
                            </div>
                          </form>
                        </div>
                      </div>
                      <div id="editor" class="grid" hidden>
                        <form id="articleForm" class="panel">
                          <h2 id="editorTitle">Articulo</h2>
                          <div class="meta-grid">
                            <div>
                              <label for="title">Title</label>
                              <input id="title" name="title" required maxlength="260">
                            </div>
                            <div>
                              <label for="slug">Slug</label>
                              <input id="slug" name="slug" required maxlength="280">
                            </div>
                          </div>
                          <label for="excerpt">Excerpt</label>
                          <textarea id="excerpt" name="excerpt"></textarea>
                          <label for="contentMarkdown">Content markdown</label>
                          <textarea id="contentMarkdown" name="contentMarkdown" style="min-height: 360px;"></textarea>
                          <div class="meta-grid">
                            <div>
                              <label for="category">Category</label>
                              <select id="category" name="category"></select>
                            </div>
                            <div>
                              <label for="author">Authors</label>
                              <select id="author" name="authors" multiple size="4"></select>
                              <p class="hint">El primer autor seleccionado ser&aacute; el autor principal. Puedes seleccionar varios autores.</p>
                            </div>
                          </div>
                          <div class="meta-grid">
                            <div>
                              <label for="status">Status</label>
                              <select id="status" name="status">
                                <option value="draft">draft</option>
                                <option value="review">review</option>
                                <option value="published">published</option>
                              </select>
                            </div>
                          </div>
                          <label for="canonicalUrl">Canonical URL</label>
                          <input id="canonicalUrl" name="canonicalUrl">
                          <label for="metaTitle">Meta title</label>
                          <input id="metaTitle" name="metaTitle">
                          <label for="metaDescription">Meta description</label>
                          <textarea id="metaDescription" name="metaDescription"></textarea>
                          <label style="display:flex; gap:8px; align-items:center; text-transform:none; font-size:14px; color:var(--ink);">
                            <input id="noindex" name="noindex" type="checkbox" style="width:auto; margin:0;"> noindex
                          </label>
                          <div class="toolbar" style="margin-top: 14px;">
                            <button type="submit">Guardar cambios</button>
                            <button type="button" id="publishButton">Publicar</button>
                            <button type="button" id="draftButton" class="secondary">Mover a borrador</button>
                            <button type="button" id="deleteArticleButton" class="danger">Eliminar articulo</button>
                          </div>
                        </form>
                        <div class="panel editor-sidebar">
                          <details class="side-section" open>
                            <summary>Newsletter</summary>
                            <div class="side-section-body">
                              <dl class="data-list">
                                <div><dt>Asunto</dt><dd id="newsletterSubject">-</dd></div>
                                <div><dt>Titulo</dt><dd id="newsletterArticleTitle">-</dd></div>
                                <div><dt>Excerpt</dt><dd id="newsletterArticleExcerpt">-</dd></div>
                                <div><dt>URL</dt><dd id="newsletterArticleUrl">-</dd></div>
                              </dl>
                              <div class="toolbar">
                                <button type="button" id="sendNewsletterButton">Enviar aviso a suscriptores</button>
                              </div>
                              <p id="newsletterSendResult" class="hint"></p>
                            </div>
                          </details>
                """.concat("""
                          <details class="side-section" open>
                            <summary>Imagen principal</summary>
                            <div class="side-section-body">
                              <div id="coverStatus" class="image-status">Sin imagen principal</div>
                              <div id="coverWarnings"></div>
                              <div id="currentCover"></div>
                              <form id="coverMetadataForm">
                                <label for="altText">Alt text obligatorio</label>
                                <input id="altText" name="altText" required>
                                <label for="caption">Caption</label>
                                <input id="caption" name="caption">
                                <label for="credit">Credit</label>
                                <input id="credit" name="credit">
                                <label for="sourceUrl">Source URL</label>
                                <input id="sourceUrl" name="sourceUrl">
                                <label for="rightsNotes">Rights notes</label>
                                <textarea id="rightsNotes" name="rightsNotes"></textarea>
                                <p class="hint">Recomendacion cover: maximo 1600px de ancho, preferente webp, intenta quedar por debajo de 400 KB cuando sea viable.</p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit" class="secondary">Guardar datos de imagen</button>
                                  <button type="button" id="removeCoverButton" class="danger">Quitar imagen principal</button>
                                </div>
                              </form>
                              <form id="coverForm">
                                <label for="coverFile">File</label>
                                <input id="coverFile" name="file" type="file" accept="image/jpeg,image/png,image/webp,image/avif">
                                <p id="coverFileHint" class="hint"></p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Cambiar imagen principal</button>
                                </div>
                              </form>
                              <h3 style="margin-top: 18px;">Importar imagen desde URL</h3>
                              <p class="notice">Importar una imagen no confirma que tengas derechos. Usa solo imagenes propias, autorizadas, de press kit permitido, dominio publico o licencia compatible.</p>
                              <form id="coverImportForm">
                                <label for="coverImportUrl">URL de imagen</label>
                                <input id="coverImportUrl" name="imageUrl" type="url" placeholder="https://...">
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Importar cover</button>
                                </div>
                              </form>
                            </div>
                          </details>
                          <details class="side-section" open>
                            <summary>Im&aacute;genes de cuerpo</summary>
                            <div class="side-section-body">
                              <p class="hint">Recomendacion cuerpo: maximo 1200px de ancho, preferente webp. Despues de subir o importar, copia el snippet Markdown y pegalo en Content markdown.</p>
                              <div class="body-metadata">
                                <label for="bodyAltText">Alt text obligatorio</label>
                                <input id="bodyAltText" name="bodyAltText" required>
                                <label for="bodyCaption">Caption</label>
                                <input id="bodyCaption" name="bodyCaption">
                                <label for="bodyCredit">Credit</label>
                                <input id="bodyCredit" name="bodyCredit">
                                <label for="bodySourceUrl">Source URL</label>
                                <input id="bodySourceUrl" name="bodySourceUrl">
                                <label for="bodyRightsNotes">Rights notes</label>
                                <textarea id="bodyRightsNotes" name="bodyRightsNotes"></textarea>
                              </div>
                              <form id="bodyImageForm">
                                <label for="bodyImageFile">File</label>
                                <input id="bodyImageFile" name="file" type="file" accept="image/jpeg,image/png,image/webp,image/avif">
                                <p id="bodyFileHint" class="hint"></p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Subir imagen de cuerpo</button>
                                </div>
                              </form>
                              <form id="bodyImportForm">
                                <label for="bodyImportUrl">Importar imagen de cuerpo desde URL</label>
                                <input id="bodyImportUrl" name="imageUrl" type="url" placeholder="https://...">
                                <p class="notice">Importa solo im&aacute;genes propias, autorizadas, de prensa permitida, dominio p&uacute;blico o licencia compatible.</p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Importar imagen de cuerpo</button>
                                </div>
                              </form>
                              <div id="bodyImages"></div>
                            </div>
                          </details>
                          <details class="side-section">
                            <summary>Audio y v&iacute;deo</summary>
                            <div class="side-section-body">
                              <p class="notice">Sube solo audio o v&iacute;deo propio, autorizado, de prensa permitida, dominio p&uacute;blico o con licencia compatible. Para trailers o entrevistas externas, es preferible insertar el enlace oficial de YouTube/Vimeo en el Markdown.</p>
                              <div class="media-metadata">
                                <label for="mediaTitle">Title</label>
                                <input id="mediaTitle" name="mediaTitle">
                                <label for="mediaCaption">Caption / description</label>
                                <input id="mediaCaption" name="mediaCaption">
                                <label for="mediaCredit">Credit</label>
                                <input id="mediaCredit" name="mediaCredit">
                                <label for="mediaSourceUrl">Source URL</label>
                                <input id="mediaSourceUrl" name="mediaSourceUrl">
                                <label for="mediaRightsNotes">Rights notes</label>
                                <textarea id="mediaRightsNotes" name="mediaRightsNotes"></textarea>
                              </div>
                              <form id="mediaAudioForm">
                                <label for="mediaAudioFile">Archivo de audio</label>
                                <input id="mediaAudioFile" name="file" type="file" accept=".mp3,.m4a,.wav,.ogg,audio/mpeg,audio/mp4,audio/x-m4a,audio/wav,audio/ogg">
                                <p id="mediaAudioFileHint" class="hint"></p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Subir audio</button>
                                </div>
                              </form>
                              <form id="mediaVideoForm">
                                <label for="mediaVideoFile">Archivo de v&iacute;deo</label>
                                <input id="mediaVideoFile" name="file" type="file" accept=".mp4,.webm,.mov,video/mp4,video/webm,video/quicktime">
                                <p id="mediaVideoFileHint" class="hint"></p>
                                <div class="toolbar" style="margin-top: 14px;">
                                  <button type="submit">Subir v&iacute;deo</button>
                                </div>
                              </form>
                              <div id="mediaFiles"></div>
                            </div>
                          </details>
                          <details class="side-section">
                            <summary>Herramientas editoriales</summary>
                            <div class="side-section-body">
                              <dl class="data-list">
                                <div><dt>API publica</dt><dd><a id="publicApiLink" class="public-link" href="#" target="_blank" rel="noreferrer"></a></dd></div>
                                <div><dt>Editor visual</dt><dd><a href="/admin/tools/editor-bloques" target="_blank" rel="noreferrer" class="public-link">Editor visual de bloques</a></dd></div>
                              </dl>
                            </div>
                          </details>
                        </div>
                      </div>
                    </section>
                  </main>
                  <script>
                    const state = { status: "draft", articles: [], current: null, selectedArticleId: null, categories: [], authors: [], authorSelection: [], selectedAuthorId: null, comments: [], newsletterStatus: "ACTIVE" };
                    const list = document.getElementById("articleList");
                    const message = document.getElementById("message");
                    const pendingComments = document.getElementById("pendingComments");
                    const authorList = document.getElementById("authorList");
                    const authorForm = document.getElementById("authorForm");
                    const editor = document.getElementById("editor");
                    const articleForm = document.getElementById("articleForm");
                    const coverForm = document.getElementById("coverForm");
                    const coverMetadataForm = document.getElementById("coverMetadataForm");
                    const coverImportForm = document.getElementById("coverImportForm");
                    const bodyImageForm = document.getElementById("bodyImageForm");
                    const bodyImportForm = document.getElementById("bodyImportForm");
                    const mediaAudioForm = document.getElementById("mediaAudioForm");
                    const mediaVideoForm = document.getElementById("mediaVideoForm");
                    const categorySelect = document.getElementById("category");
                    const authorSelect = document.getElementById("author");
                    const newsletterStatus = document.getElementById("newsletterStatus");
                    const newsletterStats = document.getElementById("newsletterStats");
                    const newsletterSubscribers = document.getElementById("newsletterSubscribers");

                    document.querySelectorAll("[data-status]").forEach((button) => {
                      button.addEventListener("click", () => {
                        state.status = button.dataset.status;
                        document.querySelectorAll("[data-status]").forEach((item) => item.classList.toggle("active", item === button));
                        loadArticles();
                      });
                    });

                    document.getElementById("refreshButton").addEventListener("click", () => loadArticles({ preserveSelection: Boolean(state.selectedArticleId) }));
                    document.getElementById("refreshCommentsButton").addEventListener("click", loadPendingComments);
                    document.getElementById("refreshNewsletterButton").addEventListener("click", loadNewsletter);
                    newsletterStatus.addEventListener("change", () => {
                      state.newsletterStatus = newsletterStatus.value;
                      loadNewsletter();
                    });
                    authorSelect.addEventListener("change", syncAuthorSelection);
                    authorForm.addEventListener("submit", saveAuthor);
                    document.getElementById("newAuthorButton").addEventListener("click", clearAuthorForm);
                    document.getElementById("clearAuthorButton").addEventListener("click", clearAuthorForm);
                    document.getElementById("refreshAuthorsButton").addEventListener("click", () => loadAuthors({ preserveSelection: true }));
                    document.getElementById("publishButton").addEventListener("click", () => changeStatus("publish"));
                    document.getElementById("draftButton").addEventListener("click", () => changeStatus("draft"));
                    document.getElementById("deleteArticleButton").addEventListener("click", deleteArticle);
                    document.getElementById("sendNewsletterButton").addEventListener("click", sendNewsletterNotice);
                    document.getElementById("removeCoverButton").addEventListener("click", removeCover);
                    articleForm.addEventListener("submit", saveArticle);
                    coverForm.addEventListener("submit", uploadCover);
                    coverMetadataForm.addEventListener("submit", saveCoverMetadata);
                    coverImportForm.addEventListener("submit", importCover);
                    bodyImageForm.addEventListener("submit", uploadBodyImage);
                    bodyImportForm.addEventListener("submit", importBodyImage);
                    mediaAudioForm.addEventListener("submit", (event) => uploadMediaFile(event, "audio"));
                    mediaVideoForm.addEventListener("submit", (event) => uploadMediaFile(event, "video"));
                    document.getElementById("coverFile").addEventListener("change", (event) => showFileHint(event, "coverFileHint"));
                    document.getElementById("bodyImageFile").addEventListener("change", (event) => showFileHint(event, "bodyFileHint"));
                    document.getElementById("mediaAudioFile").addEventListener("change", (event) => showMediaFileHint(event, "mediaAudioFileHint", 100));
                    document.getElementById("mediaVideoFile").addEventListener("change", (event) => showMediaFileHint(event, "mediaVideoFileHint", 250));

                    async function api(path, options = {}) {
                      const response = await fetch(path, {
                        headers: { Accept: "application/json", ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }) },
                        ...options,
                      });
                      if (!response.ok) {
                        const text = await response.text();
                        try {
                          const error = JSON.parse(text);
                          throw new Error(error.message || text || `${response.status} ${response.statusText}`);
                        } catch (parseError) {
                          if (parseError instanceof SyntaxError) {
                            throw new Error(text || `${response.status} ${response.statusText}`);
                          }
                          throw parseError;
                        }
                      }
                      if (response.status === 204) {
                        return null;
                      }
                      return response.json();
                    }

                    async function loadCategories() {
                      state.categories = await api("/api/categories");
                      categorySelect.innerHTML = state.categories
                        .map((category) => `<option value="${escapeHtml(category.slug)}">${escapeHtml(category.name)} (${escapeHtml(category.slug)})</option>`)
                        .join("");
                    }

                    async function loadAuthors(options = {}) {
                      const preserveSelection = Boolean(options.preserveSelection);
                      const selectedArticleAuthors = preserveSelection ? selectedAuthorIds() : [];
                      const selectedManagedAuthor = preserveSelection ? state.selectedAuthorId : null;
                      state.authors = await api("/api/admin/authors");
                      authorSelect.innerHTML = state.authors
                        .map((author) => `<option value="${author.id}">${escapeHtml(author.name)} (${escapeHtml(author.slug)})</option>`)
                        .join("");
                      renderAuthors();
                      if (selectedManagedAuthor && state.authors.some((author) => author.id === selectedManagedAuthor)) {
                        editAuthor(selectedManagedAuthor);
                      } else if (!state.selectedAuthorId) {
                        clearAuthorForm();
                      }
                      if (selectedArticleAuthors.length) {
                        selectAuthorIds(selectedArticleAuthors.filter((id) => state.authors.some((author) => author.id === id)));
                      } else {
                        selectDefaultAuthor();
                      }
                    }

                    function renderAuthors() {
                      authorList.innerHTML = state.authors.length
                        ? state.authors.map((author) => `
                            <button type="button" class="author-row" data-author-id="${author.id}">
                              <strong>${escapeHtml(author.name || "(sin nombre)")}</strong>
                              <span>${escapeHtml(author.slug || "")}</span>
                            </button>
                          `).join("")
                        : `<p class="empty">Sin autores.</p>`;
                      authorList.querySelectorAll("[data-author-id]").forEach((button) => {
                        const id = Number(button.dataset.authorId);
                        button.addEventListener("click", () => editAuthor(id));
                        button.classList.toggle("active", id === state.selectedAuthorId);
                      });
                    }

                    function editAuthor(id) {
                      const author = state.authors.find((item) => item.id === Number(id));
                      if (!author) {
                        return;
                      }
                      state.selectedAuthorId = author.id;
                      authorForm.dataset.authorId = String(author.id);
                      document.getElementById("authorFormTitle").textContent = `Editar autor #${author.id}`;
                      document.getElementById("saveAuthorButton").textContent = "Actualizar autor";
                      document.getElementById("authorName").value = author.name || "";
                      document.getElementById("authorSlug").value = author.slug || "";
                      document.getElementById("authorRole").value = author.role || "";
                      document.getElementById("authorBio").value = author.bio || "";
                      document.getElementById("authorAvatarUrl").value = author.avatarUrl || "";
                      document.getElementById("authorWebsiteUrl").value = author.websiteUrl || "";
                      document.getElementById("authorLetterboxdUrl").value = author.letterboxdUrl || "";
                      renderAuthors();
                    }

                    function clearAuthorForm() {
                      state.selectedAuthorId = null;
                      delete authorForm.dataset.authorId;
                      authorForm.reset();
                      document.getElementById("authorFormTitle").textContent = "Nuevo autor";
                      document.getElementById("saveAuthorButton").textContent = "Crear autor";
                      renderAuthors();
                    }

                    function authorPayload() {
                      return {
                        name: document.getElementById("authorName").value.trim(),
                        slug: document.getElementById("authorSlug").value.trim(),
                        role: document.getElementById("authorRole").value.trim(),
                        bio: document.getElementById("authorBio").value.trim(),
                        avatarUrl: document.getElementById("authorAvatarUrl").value.trim(),
                        websiteUrl: document.getElementById("authorWebsiteUrl").value.trim(),
                        letterboxdUrl: document.getElementById("authorLetterboxdUrl").value.trim(),
                      };
                    }

                    async function saveAuthor(event) {
                      event.preventDefault();
                      const id = authorForm.dataset.authorId;
                      const creating = !id;
                      setMessage(creating ? "Creando autor..." : "Actualizando autor...");
                      try {
                        const author = await api(creating ? "/api/admin/authors" : `/api/admin/authors/${id}`, {
                          method: creating ? "POST" : "PUT",
                          body: JSON.stringify(authorPayload()),
                        });
                        state.selectedAuthorId = author.id;
                        await loadAuthors({ preserveSelection: true });
                        editAuthor(author.id);
                        setMessage(creating ? "Autor creado." : "Autor actualizado.");
                      } catch (error) {
                        setMessage(`Error guardando autor: ${error.message}`);
                      }
                    }

                    function selectDefaultAuthor() {
                      const defaultAuthor = state.authors.find((author) => author.slug === "jose-luis-olmedo") || state.authors[0];
                      if (defaultAuthor?.id) {
                        selectAuthorIds([defaultAuthor.id]);
                      }
                    }

                    function selectAuthorIds(authorIds) {
                      const ids = new Set((authorIds || []).map((id) => String(id)).filter(Boolean));
                      authorSelect.querySelectorAll("option").forEach((option) => {
                        option.selected = ids.has(option.value);
                      });
                      state.authorSelection = Array.from(ids)
                        .map((id) => Number(id))
                        .filter((id) => Number.isFinite(id));
                      if (!state.authorSelection.length && state.authors.length) {
                        selectDefaultAuthor();
                      }
                    }

                    function syncAuthorSelection() {
                      const selected = selectedAuthorIdsFromDom();
                      state.authorSelection = state.authorSelection.filter((id) => selected.includes(id));
                      selected.forEach((id) => {
                        if (!state.authorSelection.includes(id)) {
                          state.authorSelection.push(id);
                        }
                      });
                      if (!state.authorSelection.length) {
                        selectDefaultAuthor();
                      }
                    }

                    function selectedAuthorIdsFromDom() {
                      return Array.from(authorSelect.selectedOptions)
                        .map((option) => Number(option.value))
                        .filter((id) => Number.isFinite(id));
                    }

                    function selectedAuthorIds() {
                      const selected = selectedAuthorIdsFromDom();
                      return state.authorSelection.filter((id) => selected.includes(id));
                    }

                    function clearArticleSelection() {
                      state.current = null;
                      state.selectedArticleId = null;
                      delete articleForm.dataset.articleId;
                      editor.hidden = true;
                    }

                    function setCurrentArticle(article) {
                      state.current = article;
                      state.selectedArticleId = article?.id ? String(article.id) : null;
                      if (state.selectedArticleId) {
                        articleForm.dataset.articleId = state.selectedArticleId;
                      } else {
                        delete articleForm.dataset.articleId;
                      }
                    }

                    function currentArticleId() {
                      if (!state.selectedArticleId && state.current?.id) {
                        state.selectedArticleId = String(state.current.id);
                      }
                      if (!state.selectedArticleId && articleForm.dataset.articleId && !editor.hidden) {
                        state.selectedArticleId = articleForm.dataset.articleId;
                      }
                      return state.selectedArticleId;
                    }

                    function selectedArticleIdOrWarn() {
                      const articleId = currentArticleId();
                      if (!articleId) {
                        setMessage("Selecciona un articulo antes de continuar.");
                        return null;
                      }
                      return articleId;
                    }

                    function savedArticleIdOrWarn(actionLabel = "usar esta accion") {
                      const articleId = currentArticleId();
                      if (!articleId) {
                        setMessage(`Guarda primero el articulo antes de ${actionLabel}.`);
                        return null;
                      }
                      return articleId;
                    }

                    function syncStatusTabs() {
                      document.querySelectorAll("[data-status]").forEach((item) => item.classList.toggle("active", item.dataset.status === state.status));
                    }

                    function articlePayload() {
                      const authorIds = selectedAuthorIds();
                      return {
                        title: document.getElementById("title").value.trim(),
                        slug: document.getElementById("slug").value.trim(),
                        excerpt: document.getElementById("excerpt").value,
                        content_markdown: document.getElementById("contentMarkdown").value,
                        category: document.getElementById("category").value,
                        author_id: authorIds.length ? authorIds[0] : null,
                        authorIds,
                        status: document.getElementById("status").value,
                        canonical_url: emptyToNull(document.getElementById("canonicalUrl").value),
                        meta_title: emptyToNull(document.getElementById("metaTitle").value),
                        meta_description: emptyToNull(document.getElementById("metaDescription").value),
                        noindex: document.getElementById("noindex").checked,
                      };
                    }

                    async function loadArticles(options = {}) {
                      const preserveSelection = Boolean(options.preserveSelection);
                      setMessage(`Cargando ${state.status}...`);
                      if (!preserveSelection) {
                        clearArticleSelection();
                      }
                      try {
                        state.articles = await api(`/api/admin/articles?status=${encodeURIComponent(state.status)}`);
                        renderList();
                        if (!preserveSelection || !state.selectedArticleId) {
                          setMessage(state.articles.length ? "Selecciona un articulo." : "No hay articulos en este estado.");
                        }
                      } catch (error) {
                        setMessage(`Error cargando articulos: ${error.message}`);
                      }
                    }

                    async function loadPendingComments() {
                      pendingComments.innerHTML = `<p class="empty">Cargando comentarios...</p>`;
                      try {
                        state.comments = await api("/api/admin/comments?status=PENDING");
                        renderPendingComments();
                      } catch (error) {
                        pendingComments.innerHTML = `<p class="empty">Error cargando comentarios: ${escapeHtml(error.message)}</p>`;
                      }
                    }

                    async function loadNewsletter() {
                      newsletterSubscribers.innerHTML = `<p class="empty">Cargando suscriptores...</p>`;
                      try {
                        const data = await api(`/api/admin/newsletter/subscribers?status=${encodeURIComponent(state.newsletterStatus)}&limit=200`);
                        renderNewsletter(data);
                      } catch (error) {
                        newsletterSubscribers.innerHTML = `<p class="empty">Error cargando newsletter: ${escapeHtml(error.message)}</p>`;
                      }
                    }

                    function renderNewsletter(data) {
                      const totals = data.totals || {};
                      newsletterStats.innerHTML = `
                        <div class="stat"><strong>${Number(totals.ACTIVE || 0)}</strong><span>Activos</span></div>
                        <div class="stat"><strong>${Number(totals.PENDING_CONFIRMATION || 0)}</strong><span>Pendientes</span></div>
                        <div class="stat"><strong>${Number(totals.UNSUBSCRIBED || 0)}</strong><span>Bajas</span></div>
                      `;
                      const rows = data.subscribers || [];
                      newsletterSubscribers.innerHTML = rows.length
                        ? rows.map((subscriber) => `
                            <div class="subscriber-row">
                              <div>
                                <strong>${escapeHtml(subscriber.email || "")}</strong>
                                <span>${escapeHtml(subscriber.status || "")} - alta ${formatDate(subscriber.createdAt)}</span>
                              </div>
                              <span>${escapeHtml(subscriber.source || "")}</span>
                            </div>
                          `).join("")
                        : `<p class="empty">No hay suscriptores en este estado.</p>`;
                    }

                    function renderPendingComments() {
                      pendingComments.innerHTML = state.comments.length
                        ? state.comments.map((comment) => `
                            <article class="comment-card" data-comment-id="${comment.id}">
                              <header>
                                <strong>${escapeHtml(comment.publicName || "(sin nombre)")}</strong>
                                <small>${escapeHtml(comment.article || "")} - ${escapeHtml(comment.slug || "")} - ${formatDate(comment.createdAt)}</small>
                              </header>
                              <p>${escapeHtml(comment.body || "")}</p>
                              <div class="toolbar" style="margin-bottom:0;">
                                <button type="button" data-comment-action="approve">Aprobar</button>
                                <button type="button" class="secondary" data-comment-action="reject">Rechazar</button>
                                <button type="button" class="danger" data-comment-action="delete">Eliminar</button>
                              </div>
                            </article>
                          `).join("")
                        : `<p class="empty">No hay comentarios pendientes.</p>`;
                      pendingComments.querySelectorAll("[data-comment-action]").forEach((button) => {
                        button.addEventListener("click", () => moderateComment(button));
                      });
                    }

                    async function moderateComment(button) {
                      const card = button.closest("[data-comment-id]");
                      const id = card.dataset.commentId;
                      const action = button.dataset.commentAction;
                      if (action === "delete" && !confirm("Eliminar este comentario pendiente de forma definitiva. Continuar?")) {
                        return;
                      }
                      setMessage(`Moderando comentario #${id}...`);
                      try {
                        const method = action === "delete" ? "DELETE" : "PATCH";
                        const path = action === "delete" ? `/api/admin/comments/${id}` : `/api/admin/comments/${id}/${action}`;
                        await api(path, { method });
                        await loadPendingComments();
                        setMessage("Comentario actualizado.");
                      } catch (error) {
                        setMessage(`Error moderando comentario: ${error.message}`);
                      }
                    }

                    function renderList() {
                      list.innerHTML = state.articles.length
                        ? state.articles.map((article) => `
                            <button type="button" class="article-row" data-id="${article.id}">
                              <strong>${escapeHtml(article.title || "(sin titulo)")}</strong>
                              <span>${escapeHtml(article.slug || "")}</span>
                            </button>
                          `).join("")
                        : `<p class="empty">Sin articulos.</p>`;
                      list.querySelectorAll("[data-id]").forEach((button) => {
                        button.addEventListener("click", () => openArticle(button.dataset.id));
                        button.classList.toggle("active", button.dataset.id === String(state.selectedArticleId || ""));
                      });
                    }

                    async function openArticle(id) {
                      if (!id) {
                        setMessage("Selecciona un articulo antes de continuar.");
                        return;
                      }
                      setMessage("Abriendo articulo...");
                      try {
                        const article = await api(`/api/admin/articles/${id}`);
                        setCurrentArticle(article);
                        list.querySelectorAll("[data-id]").forEach((button) => button.classList.toggle("active", button.dataset.id === String(state.selectedArticleId)));
                        fillForm(article);
                        editor.hidden = false;
                        setMessage(`Articulo #${article.id} abierto.`);
                      } catch (error) {
                        setMessage(`Error abriendo articulo: ${error.message}`);
                      }
                    }

                    function fillForm(article) {
                      document.getElementById("editorTitle").textContent = `Articulo #${article.id}`;
                      document.getElementById("title").value = article.title || "";
                      document.getElementById("slug").value = article.slug || "";
                      document.getElementById("excerpt").value = article.excerpt || "";
                      document.getElementById("contentMarkdown").value = article.content_markdown || "";
                      document.getElementById("category").value = article.category || "cultura";
                      const authorIds = Array.isArray(article.authorIds) && article.authorIds.length
                        ? article.authorIds
                        : (article.author_id ? [article.author_id] : []);
                      selectAuthorIds(authorIds);
                      document.getElementById("status").value = article.status || "draft";
                      document.getElementById("canonicalUrl").value = article.canonical_url || "";
                      document.getElementById("metaTitle").value = article.meta_title || "";
                      document.getElementById("metaDescription").value = article.meta_description || "";
                      document.getElementById("noindex").checked = Boolean(article.noindex);
                      const publicPath = `/api/articles/${encodeURIComponent(article.slug || "")}`;
                      const publicLink = document.getElementById("publicApiLink");
                      publicLink.href = publicPath;
                      publicLink.textContent = `${window.location.origin}${publicPath}`;
                      fillNewsletterPreview(article);
                      fillImageMetadata(article.cover);
                      renderCoverWarnings(article.cover);
                      renderCurrentCover(article.cover);
                      renderBodyImages(article.bodyImages || []);
                      renderMediaFiles(article.mediaFiles || []);
                      document.getElementById("removeCoverButton").disabled = !article.cover;
                      document.getElementById("deleteArticleButton").disabled = article.status === "published";
                      document.getElementById("sendNewsletterButton").disabled = article.status !== "published";
                    }

                    function fillNewsletterPreview(article) {
                      const publicArticleUrl = `https://reuniondearte.com/articulos/${article.slug || ""}`;
                      document.getElementById("newsletterSubject").textContent = `Nuevo articulo en Reunion de Arte: ${article.title || ""}`;
                      document.getElementById("newsletterArticleTitle").textContent = article.title || "-";
                      document.getElementById("newsletterArticleExcerpt").textContent = article.excerpt || "-";
                      document.getElementById("newsletterArticleUrl").innerHTML = `<a class="public-link" href="${escapeAttribute(publicArticleUrl)}" target="_blank" rel="noreferrer">${escapeHtml(publicArticleUrl)}</a>`;
                      document.getElementById("newsletterSendResult").textContent = article.status === "published" ? "" : "Solo se puede enviar desde articulos publicados.";
                    }

                    function fillImageMetadata(cover) {
                      document.getElementById("altText").value = cover?.coverAlt || "";
                      document.getElementById("caption").value = cover?.coverCaption || "";
                      document.getElementById("credit").value = cover?.coverCredit || "";
                      document.getElementById("sourceUrl").value = cover?.sourceUrl || "";
                      document.getElementById("rightsNotes").value = cover?.rightsNotes || "";
                      document.getElementById("coverFile").value = "";
                      document.getElementById("coverImportUrl").value = "";
                      document.getElementById("coverFileHint").textContent = "";
                      document.getElementById("bodyImageFile").value = "";
                      document.getElementById("bodyImportUrl").value = "";
                      document.getElementById("bodyFileHint").textContent = "";
                      fillBodyImageMetadata(null);
                      fillMediaFileMetadata(null);
                      document.getElementById("mediaAudioFile").value = "";
                      document.getElementById("mediaVideoFile").value = "";
                      document.getElementById("mediaAudioFileHint").textContent = "";
                      document.getElementById("mediaVideoFileHint").textContent = "";
                    }

                    function fillBodyImageMetadata(image) {
                      document.getElementById("bodyAltText").value = image?.altText || "";
                      document.getElementById("bodyCaption").value = image?.caption || "";
                      document.getElementById("bodyCredit").value = image?.credit || "";
                      document.getElementById("bodySourceUrl").value = image?.sourceUrl || "";
                      document.getElementById("bodyRightsNotes").value = image?.rightsNotes || "";
                    }

                    function fillMediaFileMetadata(file) {
                      document.getElementById("mediaTitle").value = file?.title || "";
                      document.getElementById("mediaCaption").value = file?.caption || "";
                      document.getElementById("mediaCredit").value = file?.credit || "";
                      document.getElementById("mediaSourceUrl").value = file?.sourceUrl || "";
                      document.getElementById("mediaRightsNotes").value = file?.rightsNotes || "";
                    }

                    function renderCurrentCover(cover) {
                      const status = document.getElementById("coverStatus");
                      const container = document.getElementById("currentCover");
                      status.textContent = cover ? "Imagen principal asignada" : "Sin imagen principal";
                      status.classList.toggle("assigned", Boolean(cover));
                      if (!cover) {
                        container.innerHTML = `<p class="empty">Este articulo todavia no tiene imagen principal. Puedes subir una nueva o importarla desde URL.</p>`;
                        return;
                      }
                      container.innerHTML = `
                        <img class="image-preview cover-preview" src="${escapeAttribute(cover.coverImage)}" alt="${escapeAttribute(cover.coverAlt || "")}">
                        <dl class="data-list">
                          <div><dt>URL publica</dt><dd><a class="public-link" href="${escapeAttribute(cover.coverImage)}" target="_blank" rel="noreferrer">${escapeHtml(cover.coverImage)}</a></dd></div>
                          <div><dt>Alt text</dt><dd>${escapeHtml(cover.coverAlt || "-")}</dd></div>
                          <div><dt>Caption</dt><dd>${escapeHtml(cover.coverCaption || "-")}</dd></div>
                          <div><dt>Credit</dt><dd>${escapeHtml(cover.coverCredit || "-")}</dd></div>
                          <div><dt>Source URL</dt><dd>${linkOrDash(cover.sourceUrl)}</dd></div>
                          <div><dt>Rights notes</dt><dd>${escapeHtml(cover.rightsNotes || "-")}</dd></div>
                          <div><dt>Peso</dt><dd>${formatBytes(cover.size_bytes)}${imageSizeLabel(cover)}</dd></div>
                        </dl>
                      `;
                    }

                    function renderCoverWarnings(cover) {
                      const container = document.getElementById("coverWarnings");
                      if (!cover) {
                        container.innerHTML = "";
                        return;
                      }
                      const warnings = [];
                      if (!cover.coverAlt) warnings.push("Falta alt text para accesibilidad y contexto editorial.");
                      if (!cover.coverCredit) warnings.push("Falta credit; revisa atribucion antes de publicar.");
                      if (!cover.sourceUrl) warnings.push("Source URL vacio; dejalo documentado si existe fuente/licencia externa.");
                      if (!cover.rightsNotes) warnings.push("Rights notes vacio; recomendable para control legal.");
                      container.innerHTML = warnings.length
                        ? `<div class="warning-list">${warnings.map((warning) => `<div>${escapeHtml(warning)}</div>`).join("")}</div>`
                        : "";
                    }

                    function renderBodyImages(images) {
                      const container = document.getElementById("bodyImages");
                      if (!images.length) {
                        container.innerHTML = `<p class="empty">Sin imagenes del cuerpo asociadas.</p>`;
                        return;
                      }
                      container.innerHTML = images.map((image, index) => {
                        const snippetId = `snippet-${image.id || image.mediaAssetId}`;
                        return `
                        <div class="body-image" data-body-image-id="${image.id || ""}">
                          <img class="image-preview body-thumbnail" src="${escapeAttribute(image.publicUrl)}" alt="${escapeAttribute(image.altText || "")}">
                          <div class="asset-card-body">
                            <h4 class="asset-title">${escapeHtml(image.altText || "Imagen de cuerpo " + (index + 1))}</h4>
                            <div class="asset-summary">
                              <a class="public-link" href="${escapeAttribute(image.publicUrl)}" target="_blank" rel="noreferrer">${escapeHtml(image.publicUrl)}</a><br>
                              ${formatBytes(image.size_bytes)}${imageSizeLabel(image)}
                            </div>
                            <label for="${snippetId}">Snippet Markdown</label>
                            <textarea id="${snippetId}" class="snippet compact-snippet" readonly>${escapeHtml(image.markdownSnippet || "")}</textarea>
                            <div class="toolbar compact-actions">
                              <button type="button" class="secondary" data-copy="${snippetId}">Copiar snippet</button>
                              <button type="button" class="danger" data-remove-body-image="${image.id || ""}" ${image.id ? "" : "disabled"}>Quitar del articulo</button>
                            </div>
                            <details class="asset-legal">
                              <summary>Metadatos legales</summary>
                              <dl class="data-list">
                                <div><dt>Alt text</dt><dd>${escapeHtml(image.altText || "-")}</dd></div>
                                <div><dt>Caption</dt><dd>${escapeHtml(image.caption || "-")}</dd></div>
                                <div><dt>Credit</dt><dd>${escapeHtml(image.credit || "-")}</dd></div>
                                <div><dt>Source URL</dt><dd>${linkOrDash(image.sourceUrl)}</dd></div>
                                <div><dt>Rights notes</dt><dd>${escapeHtml(image.rightsNotes || "-")}</dd></div>
                                <div><dt>Active</dt><dd>${image.active === false ? "no" : "yes"}</dd></div>
                                <div><dt>Created at</dt><dd>${formatDate(image.createdAt)}</dd></div>
                              </dl>
                            </details>
                          </div>
                        </div>
                      `;
                      }).join("");
                      container.querySelectorAll("[data-copy]").forEach((button) => {
                        button.addEventListener("click", async () => {
                          const textarea = document.getElementById(button.dataset.copy);
                          await navigator.clipboard.writeText(textarea.value);
                          setMessage("Snippet Markdown copiado.");
                        });
                      });
                      container.querySelectorAll("[data-remove-body-image]").forEach((button) => {
                        button.addEventListener("click", () => removeBodyImage(button.dataset.removeBodyImage));
                      });
                    }

                    function renderMediaFiles(files) {
                      const container = document.getElementById("mediaFiles");
                      if (!files.length) {
                        container.innerHTML = `<p class="empty">Sin audio ni video asociado.</p>`;
                        return;
                      }
                      container.innerHTML = files.map((file, index) => {
                        const snippetId = `media-snippet-${file.id || file.mediaAssetId}`;
                        const kindLabel = file.kind === "audio" ? "Audio" : "Video";
                        return `
                        <div class="media-file" data-media-file-id="${file.id || ""}">
                          <h4 class="asset-title">${kindLabel} ${index + 1}: ${escapeHtml(file.title || file.publicUrl || "-")}</h4>
                          <div class="asset-summary">
                            <a class="public-link" href="${escapeAttribute(file.publicUrl)}" target="_blank" rel="noreferrer">${escapeHtml(file.publicUrl)}</a>
                          </div>
                          <label for="${snippetId}">Snippet Markdown</label>
                          <textarea id="${snippetId}" class="snippet compact-snippet" readonly>${escapeHtml(file.markdownSnippet || "")}</textarea>
                          <div class="toolbar compact-actions">
                            <button type="button" class="secondary" data-copy="${snippetId}">Copiar snippet</button>
                            <button type="button" class="danger" data-remove-media-file="${file.id || ""}" ${file.id ? "" : "disabled"}>Quitar del articulo</button>
                          </div>
                          <details class="asset-legal">
                            <summary>Metadatos legales</summary>
                            <dl class="data-list">
                              <div><dt>Tipo</dt><dd>${escapeHtml(file.kind || "-")}</dd></div>
                              <div><dt>Titulo</dt><dd>${escapeHtml(file.title || "-")}</dd></div>
                              <div><dt>Caption / description</dt><dd>${escapeHtml(file.caption || "-")}</dd></div>
                              <div><dt>Credit</dt><dd>${escapeHtml(file.credit || "-")}</dd></div>
                              <div><dt>Source URL</dt><dd>${linkOrDash(file.sourceUrl)}</dd></div>
                              <div><dt>Rights notes</dt><dd>${escapeHtml(file.rightsNotes || "-")}</dd></div>
                              <div><dt>Active</dt><dd>${file.active === false ? "no" : "yes"}</dd></div>
                              <div><dt>Created at</dt><dd>${formatDate(file.createdAt)}</dd></div>
                            </dl>
                          </details>
                        </div>
                      `;
                      }).join("");
                      container.querySelectorAll("[data-copy]").forEach((button) => {
                        button.addEventListener("click", async () => {
                          const textarea = document.getElementById(button.dataset.copy);
                          await navigator.clipboard.writeText(textarea.value);
                          setMessage("Snippet Markdown copiado.");
                        });
                      });
                      container.querySelectorAll("[data-remove-media-file]").forEach((button) => {
                        button.addEventListener("click", () => removeMediaFile(button.dataset.removeMediaFile));
                      });
                    }

                    async function saveArticle(event) {
                      event.preventDefault();
                      const articleId = currentArticleId();
                      setMessage("Guardando...");
                      const body = articlePayload();
                      try {
                        const saved = articleId
                          ? await api(`/api/admin/articles/${articleId}`, { method: "PUT", body: JSON.stringify(body) })
                          : await api("/api/admin/articles", { method: "POST", body: JSON.stringify({ ...body, status: "draft" }) });
                        setCurrentArticle(saved);
                        fillForm(saved);
                        state.status = saved.status || body.status || "draft";
                        syncStatusTabs();
                        await loadArticles({ preserveSelection: true });
                        await openArticle(saved.id || articleId);
                        setMessage(articleId ? "Cambios guardados." : "Articulo creado.");
                      } catch (error) {
                        setMessage(`Error guardando: ${error.message}`);
                      }
                    }

                    async function changeStatus(action) {
                      const articleId = savedArticleIdOrWarn(action === "publish" ? "publicar" : "mover a borrador");
                      if (!articleId) return;
                      if (action === "draft" && state.current?.status === "published") {
                        const confirmed = confirm("Mover este articulo publicado a borrador lo retirara de la API publica. No borra imagenes ni ficheros. ¿Continuar?");
                        if (!confirmed) return;
                      }
                      setMessage(action === "publish" ? "Publicando..." : "Moviendo a draft...");
                      try {
                        const updated = await api(`/api/admin/articles/${articleId}/${action}`, { method: "PATCH" });
                        setCurrentArticle(updated);
                        state.status = updated.status;
                        syncStatusTabs();
                        await loadArticles({ preserveSelection: true });
                        await openArticle(updated.id || articleId);
                        setMessage(action === "publish" ? "Articulo publicado." : "Articulo en draft.");
                      } catch (error) {
                        setMessage(`Error cambiando estado: ${error.message}`);
                      }
                    }

                    async function deleteArticle() {
                      const articleId = savedArticleIdOrWarn("eliminar");
                      if (!articleId || !state.current) return;
                      if (state.current.status === "published") {
                        setMessage("No se puede eliminar un articulo publicado. Muevelo primero a borrador.");
                        return;
                      }
                      const expected = `ELIMINAR ${state.current.slug}`;
                      const typed = prompt(`Confirmacion fuerte: escribe exactamente "${expected}" para eliminar el articulo. No se borraran ficheros de storage.`);
                      if (typed !== expected) {
                        setMessage("Eliminacion cancelada.");
                        return;
                      }
                      setMessage("Eliminando articulo...");
                      try {
                        await api(`/api/admin/articles/${articleId}`, { method: "DELETE" });
                        clearArticleSelection();
                        await loadArticles();
                        setMessage("Articulo eliminado. Los ficheros de media no se han borrado fisicamente.");
                      } catch (error) {
                        setMessage(`Error eliminando articulo: ${error.message}`);
                      }
                    }

                    async function sendNewsletterNotice() {
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      if (state.current?.status !== "published") {
                        setMessage("Solo se puede enviar newsletter de articulos publicados.");
                        return;
                      }
                      const typed = prompt('Confirmacion fuerte: escribe exactamente "ENVIAR NEWSLETTER" para enviar el aviso a suscriptores activos.');
                      if (typed !== "ENVIAR NEWSLETTER") {
                        setMessage("Envio de newsletter cancelado.");
                        return;
                      }
                      setMessage("Enviando newsletter...");
                      document.getElementById("newsletterSendResult").textContent = "Enviando...";
                      try {
                        const result = await api(`/api/admin/newsletter/articles/${articleId}/send`, {
                          method: "POST",
                          body: JSON.stringify({ confirm: "ENVIAR NEWSLETTER" }),
                        });
                        const summary = `Enviados: ${result.sent || 0}. Fallidos: ${result.failed || 0}. Omitidos: ${result.skipped || 0}.`;
                        document.getElementById("newsletterSendResult").textContent = summary;
                        await loadNewsletter();
                        setMessage(summary);
                      } catch (error) {
                        document.getElementById("newsletterSendResult").textContent = `Error: ${error.message}`;
                        setMessage(`Error enviando newsletter: ${error.message}`);
                      }
                    }

                    async function uploadCover(event) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      const file = document.getElementById("coverFile").files[0];
                      const metadata = imageMetadata();
                      if (!file || !metadata.altText) {
                        setMessage("Selecciona una imagen y escribe altText.");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", file);
                      appendMetadata(formData, metadata);
                      setMessage("Subiendo cover...");
                      try {
                        const result = await api(`/api/admin/articles/${articleId}/cover`, { method: "POST", body: formData });
                        setMessage(`Cover guardada: ${result.coverImage}`);
                        await openArticle(articleId);
                      } catch (error) {
                        setMessage(`Error subiendo cover: ${error.message}`);
                      }
                    }

                    async function saveCoverMetadata(event) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      if (!state.current?.cover) {
                        setMessage("No hay imagen principal para actualizar.");
                        return;
                      }
                      const metadata = imageMetadata();
                      if (!metadata.altText) {
                        setMessage("Alt text es obligatorio.");
                        return;
                      }
                      setMessage("Guardando datos de imagen...");
                      try {
                        await api(`/api/admin/articles/${articleId}/cover/metadata`, { method: "PATCH", body: JSON.stringify(metadata) });
                        await openArticle(articleId);
                        setMessage("Datos de imagen guardados.");
                      } catch (error) {
                        setMessage(`Error guardando datos de imagen: ${error.message}`);
                      }
                    }

                    async function removeCover() {
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId || !state.current?.cover) return;
                      const confirmed = confirm("Quitar la imagen principal desasignara la portada del articulo. No borrara el fichero fisico ni el media asset. ¿Continuar?");
                      if (!confirmed) return;
                      setMessage("Quitando imagen principal...");
                      try {
                        await api(`/api/admin/articles/${articleId}/cover`, { method: "DELETE" });
                        await openArticle(articleId);
                        setMessage("Imagen principal quitada. El fichero de storage se conserva.");
                      } catch (error) {
                        setMessage(`Error quitando imagen principal: ${error.message}`);
                      }
                    }

                    async function importCover(event) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      const body = importPayload("coverImportUrl");
                      if (!body) return;
                      setMessage("Importando cover...");
                      try {
                        const result = await api(`/api/admin/articles/${articleId}/cover/import`, { method: "POST", body: JSON.stringify(body) });
                        setMessage(`Cover importada: ${result.coverImage}`);
                        await openArticle(articleId);
                      } catch (error) {
                        setMessage(`Error importando cover: ${error.message}`);
                      }
                    }

                    async function uploadBodyImage(event) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      const file = document.getElementById("bodyImageFile").files[0];
                      const metadata = bodyImageMetadata();
                      if (!file || !metadata.altText) {
                        setMessage("Selecciona una imagen de cuerpo y escribe altText.");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", file);
                      appendMetadata(formData, metadata);
                      setMessage("Subiendo imagen de cuerpo...");
                      try {
                        const result = await api(`/api/admin/articles/${articleId}/body-images`, { method: "POST", body: formData });
                        fillBodyImageMetadata(null);
                        setMessage(`Imagen de cuerpo guardada. Snippet disponible para copiar.`);
                        await openArticle(articleId);
                      } catch (error) {
                        setMessage(`Error subiendo imagen de cuerpo: ${error.message}`);
                      }
                    }

                    async function importBodyImage(event) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      const body = importPayload("bodyImportUrl", bodyImageMetadata());
                      if (!body) return;
                      setMessage("Importando imagen de cuerpo...");
                      try {
                        const result = await api(`/api/admin/articles/${articleId}/body-images/import`, { method: "POST", body: JSON.stringify(body) });
                        fillBodyImageMetadata(null);
                        setMessage(`Imagen de cuerpo importada. Snippet disponible para copiar.`);
                        await openArticle(articleId);
                      } catch (error) {
                        setMessage(`Error importando imagen de cuerpo: ${error.message}`);
                      }
                    }

                    async function removeBodyImage(articleMediaId) {
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId || !articleMediaId) return;
                      const confirmed = confirm("Quitar esta imagen del cuerpo solo la desasociara del articulo. No borrara el fichero fisico de storage. Â¿Continuar?");
                      if (!confirmed) return;
                      setMessage("Quitando imagen de cuerpo...");
                      try {
                        await api(`/api/admin/articles/${articleId}/body-images/${articleMediaId}`, { method: "DELETE" });
                        await openArticle(articleId);
                        setMessage("Imagen de cuerpo quitada. El fichero de storage se conserva.");
                      } catch (error) {
                        setMessage(`Error quitando imagen de cuerpo: ${error.message}`);
                      }
                    }

                    async function uploadMediaFile(event, kind) {
                      event.preventDefault();
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId) return;
                      const inputId = kind === "audio" ? "mediaAudioFile" : "mediaVideoFile";
                      const file = document.getElementById(inputId).files[0];
                      if (!file) {
                        setMessage(kind === "audio" ? "Selecciona un archivo de audio." : "Selecciona un archivo de video.");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", file);
                      appendMediaMetadata(formData, mediaFileMetadata());
                      setMessage(kind === "audio" ? "Subiendo audio..." : "Subiendo video...");
                      try {
                        await api(`/api/admin/articles/${articleId}/media-files/${kind}`, { method: "POST", body: formData });
                        fillMediaFileMetadata(null);
                        document.getElementById(inputId).value = "";
                        document.getElementById(kind === "audio" ? "mediaAudioFileHint" : "mediaVideoFileHint").textContent = "";
                        await openArticle(articleId);
                        setMessage(kind === "audio" ? "Audio guardado. Snippet disponible para copiar." : "Video guardado. Snippet disponible para copiar.");
                      } catch (error) {
                        setMessage(`${kind === "audio" ? "Error subiendo audio" : "Error subiendo video"}: ${error.message}`);
                      }
                    }

                    async function removeMediaFile(articleMediaId) {
                      const articleId = savedArticleIdOrWarn("usar esta accion");
                      if (!articleId || !articleMediaId) return;
                      const confirmed = confirm("Quitar este audio/video solo lo desasociara del articulo. No borrara el fichero fisico de storage. Â¿Continuar?");
                      if (!confirmed) return;
                      setMessage("Quitando audio/video...");
                      try {
                        await api(`/api/admin/articles/${articleId}/media-files/${articleMediaId}`, { method: "DELETE" });
                        await openArticle(articleId);
                        setMessage("Audio/video quitado. El fichero de storage se conserva.");
                      } catch (error) {
                        setMessage(`Error quitando audio/video: ${error.message}`);
                      }
                    }

                    function imageMetadata() {
                      return {
                        altText: document.getElementById("altText").value.trim(),
                        caption: document.getElementById("caption").value.trim(),
                        credit: document.getElementById("credit").value.trim(),
                        sourceUrl: document.getElementById("sourceUrl").value.trim(),
                        rightsNotes: document.getElementById("rightsNotes").value.trim(),
                      };
                    }

                    function bodyImageMetadata() {
                      return {
                        altText: document.getElementById("bodyAltText").value.trim(),
                        caption: document.getElementById("bodyCaption").value.trim(),
                        credit: document.getElementById("bodyCredit").value.trim(),
                        sourceUrl: document.getElementById("bodySourceUrl").value.trim(),
                        rightsNotes: document.getElementById("bodyRightsNotes").value.trim(),
                      };
                    }

                    function mediaFileMetadata() {
                      return {
                        title: document.getElementById("mediaTitle").value.trim(),
                        caption: document.getElementById("mediaCaption").value.trim(),
                        credit: document.getElementById("mediaCredit").value.trim(),
                        sourceUrl: document.getElementById("mediaSourceUrl").value.trim(),
                        rightsNotes: document.getElementById("mediaRightsNotes").value.trim(),
                      };
                    }

                    function importPayload(inputId, metadata = imageMetadata()) {
                      const imageUrl = document.getElementById(inputId).value.trim();
                      const lowerImageUrl = imageUrl.toLowerCase();
                      if (!imageUrl || (!lowerImageUrl.startsWith("http://") && !lowerImageUrl.startsWith("https://"))) {
                        setMessage("La URL de importacion debe ser http o https.");
                        return null;
                      }
                      if (!metadata.altText || !metadata.caption || !metadata.credit || !metadata.sourceUrl || !metadata.rightsNotes) {
                        setMessage("Para importar son obligatorios alt text, caption, credit, source URL y rights notes.");
                        return null;
                      }
                      return { imageUrl, ...metadata };
                    }

                    function appendMetadata(formData, metadata) {
                      formData.append("altText", metadata.altText);
                      appendIfPresent(formData, "caption", metadata.caption);
                      appendIfPresent(formData, "credit", metadata.credit);
                      appendIfPresent(formData, "sourceUrl", metadata.sourceUrl);
                      appendIfPresent(formData, "rightsNotes", metadata.rightsNotes);
                    }

                    function appendMediaMetadata(formData, metadata) {
                      appendIfPresent(formData, "title", metadata.title);
                      appendIfPresent(formData, "caption", metadata.caption);
                      appendIfPresent(formData, "credit", metadata.credit);
                      appendIfPresent(formData, "sourceUrl", metadata.sourceUrl);
                      appendIfPresent(formData, "rightsNotes", metadata.rightsNotes);
                    }

                    function appendIfPresent(formData, key, value) {
                      if (value && value.trim()) {
                        formData.append(key, value.trim());
                      }
                    }

                    function emptyToNull(value) {
                      return value && value.trim() ? value.trim() : null;
                    }

                    function showFileHint(event, targetId) {
                      const file = event.target.files[0];
                      const target = document.getElementById(targetId);
                      if (!file) {
                        target.textContent = "";
                        return;
                      }
                      const warnings = [];
                      if (file.size > 8 * 1024 * 1024) warnings.push("supera el limite backend de 8 MB");
                      if (file.size > 400 * 1024) warnings.push("para cover, intenta optimizar por debajo de 400 KB si es viable");
                      target.textContent = `${file.name} - ${formatBytes(file.size)}${warnings.length ? " - Aviso: " + warnings.join("; ") : ""}`;
                    }

                    function showMediaFileHint(event, targetId, maxMb) {
                      const file = event.target.files[0];
                      const target = document.getElementById(targetId);
                      if (!file) {
                        target.textContent = "";
                        return;
                      }
                      const warnings = [];
                      if (file.size > maxMb * 1024 * 1024) warnings.push(`supera el limite backend de ${maxMb} MB`);
                      target.textContent = `${file.name} - ${formatBytes(file.size)}${warnings.length ? " - Aviso: " + warnings.join("; ") : ""}`;
                    }

                    function formatBytes(bytes) {
                      if (!bytes && bytes !== 0) return "-";
                      if (bytes < 1024) return `${bytes} B`;
                      if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
                      return `${(bytes / 1024 / 1024).toFixed(2)} MB`;
                    }

                    function formatDate(value) {
                      return value ? new Date(value).toLocaleString("es-ES") : "-";
                    }

                    function imageSizeLabel(image) {
                      return image && (image.width || image.height) ? ` - ${image.width || "?"}x${image.height || "?"} px` : "";
                    }

                    function linkOrDash(value) {
                      return value ? `<a class="public-link" href="${escapeAttribute(value)}" target="_blank" rel="noreferrer">${escapeHtml(value)}</a>` : "-";
                    }

                    function setMessage(value) {
                      message.textContent = value;
                    }

                    function escapeHtml(value) {
                      return String(value ?? "")
                        .replaceAll("&", "&amp;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll('"', "&quot;")
                        .replaceAll("'", "&#039;");
                    }

                    function escapeAttribute(value) {
                      return escapeHtml(value).replaceAll("`", "&#096;");
                    }

                    loadCategories()
                      .then(loadAuthors)
                      .then(loadArticles)
                      .then(loadNewsletter)
                      .then(loadPendingComments)
                      .catch((error) => setMessage(`Error inicializando admin: ${error.message}`));
                  </script>
                </body>
                </html>
                """);
    }

    @GetMapping(value = "/admin/tools/editor-bloques", produces = MediaType.TEXT_HTML_VALUE)
    public String visualBlockEditor() {
        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Editor visual de bloques - Reunion de Arte</title>
                  <style>
                    :root { color-scheme: light; --ink:#1c1917; --muted:#78716c; --line:#d6d3d1; --paper:#fffdf8; --soft:#f5f2ea; --accent:#334155; }
                    * { box-sizing: border-box; }
                    body { margin: 0; background: var(--paper); color: var(--ink); font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
                    header { border-bottom: 1px solid var(--line); background: #fff; padding: 14px 18px; }
                    header h1 { margin: 0; font-size: 20px; }
                    main { max-width: 980px; margin: 0 auto; padding: 18px; }
                    button, textarea { font: inherit; }
                    button { border: 1px solid var(--ink); background: var(--ink); color: #fff; padding: 9px 12px; cursor: pointer; }
                    button.secondary { background: #fff; color: var(--ink); border-color: var(--line); }
                    textarea { width: 100%; min-height: 420px; margin-top: 10px; border: 1px solid var(--line); background: #fff; color: var(--ink); padding: 12px; resize: vertical; line-height: 1.45; font-family: ui-monospace, SFMono-Regular, Consolas, "Liberation Mono", monospace; font-size: 13px; }
                    .panel { border: 1px solid var(--line); background: #fff; padding: 14px; margin-top: 14px; }
                    .toolbar { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin: 10px 0 0; }
                    .notice { border-left: 3px solid #92400e; background: #fffbeb; color: #713f12; padding: 10px 12px; font-size: 13px; line-height: 1.45; }
                    .hint { color: var(--muted); font-size: 13px; line-height: 1.45; }
                    .steps { margin: 8px 0 0 20px; padding: 0; line-height: 1.5; }
                    .copy-status { min-height: 20px; color: #166534; font-size: 13px; }
                    .public-link { color: #1d4ed8; font-size: 14px; }
                  </style>
                </head>
                <body>
                  <header><h1>Editor visual de bloques</h1></header>
                  <main>
                    <p><a href="/admin" class="public-link">Volver al admin</a></p>
                    <p class="notice">Usa solo im&aacute;genes propias, press kit oficial, cesi&oacute;n escrita o licencias compatibles. No uses Google, Letterboxd, Instagram, prensa, blogs o redes sin permiso claro.</p>
                    <section class="panel">
                      <h2 style="margin:0; font-size:16px;">Plantillas</h2>
                      <p class="hint">Elige una plantilla, cambia los textos y sustituye cada <strong>image:</strong> por la URL p&uacute;blica legal de R2.</p>
                      <div class="toolbar">
                        <button type="button" class="secondary" data-template="posterGrid">Grid p&oacute;ster</button>
                        <button type="button" class="secondary" data-template="landscapeGrid">Grid panor&aacute;mico</button>
                        <button type="button" class="secondary" data-template="ranking">Ranking</button>
                        <button type="button" class="secondary" data-template="gallery">Galer&iacute;a</button>
                      </div>
                    </section>
                    <section class="panel">
                      <h2 style="margin:0; font-size:16px;">Markdown</h2>
                      <textarea id="markdownOutput" spellcheck="false"></textarea>
                      <div class="toolbar">
                        <button type="button" id="copyMarkdownButton">Copiar Markdown</button>
                      </div>
                      <p id="copyStatus" class="copy-status"></p>
                    </section>
                    <section class="panel">
                      <h2 style="margin:0; font-size:16px;">Uso</h2>
                      <ol class="steps">
                        <li>Sube/importa im&aacute;genes legales desde el admin.</li>
                        <li>Copia la URL p&uacute;blica de R2.</li>
                        <li>P&eacute;gala en image: dentro del bloque.</li>
                        <li>Copia el Markdown final.</li>
                        <li>Vuelve al art&iacute;culo y p&eacute;galo en contentMarkdown.</li>
                      </ol>
                    </section>
                  </main>
                """.concat("""
                  <script>
                    const markdownOutput = document.getElementById("markdownOutput");
                    const copyStatus = document.getElementById("copyStatus");
                    const templates = {
                      posterGrid: [
                        `:::rda-grid variant="poster" columns="5" title="Selección visual"`,
                        `- image: https://placehold.co/400x600?text=Poster+1`,
                        `  alt: Póster genérico de prueba 1`,
                        `  title: Obra 1`,
                        `  href: /articulos/`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/400x600?text=Poster+2`,
                        `  alt: Póster genérico de prueba 2`,
                        `  title: Obra 2`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/400x600?text=Poster+3`,
                        `  alt: Póster genérico de prueba 3`,
                        `  title: Obra 3`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        `:::`
                      ].join("\\n"),
                      landscapeGrid: [
                        `:::rda-grid variant="landscape" columns="3" title="Selección visual"`,
                        `- image: https://placehold.co/1200x675?text=Imagen+1`,
                        `  alt: Imagen panorámica de prueba 1`,
                        `  title: Obra 1`,
                        `  href: /articulos/`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/1200x675?text=Imagen+2`,
                        `  alt: Imagen panorámica de prueba 2`,
                        `  title: Obra 2`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/1200x675?text=Imagen+3`,
                        `  alt: Imagen panorámica de prueba 3`,
                        `  title: Obra 3`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        `:::`
                      ].join("\\n"),
                      ranking: [
                        `:::rda-ranking title="Ranking editorial"`,
                        `- rank: 1`,
                        `  title: Posición de ejemplo 1`,
                        `  image: https://placehold.co/400x600?text=Rank+1`,
                        `  alt: Imagen de ejemplo 1`,
                        `  text: Texto breve de ejemplo.`,
                        ``,
                        `- rank: 2`,
                        `  title: Posición de ejemplo 2`,
                        `  image: https://placehold.co/400x600?text=Rank+2`,
                        `  alt: Imagen de ejemplo 2`,
                        `  text: Texto breve de ejemplo.`,
                        ``,
                        `- rank: 3`,
                        `  title: Posición de ejemplo 3`,
                        `  image: https://placehold.co/400x600?text=Rank+3`,
                        `  alt: Imagen de ejemplo 3`,
                        `  text: Texto breve de ejemplo.`,
                        `:::`
                      ].join("\\n"),
                      gallery: [
                        `:::rda-gallery variant="landscape" columns="3" title="Galería editorial"`,
                        `- image: https://placehold.co/1200x675?text=Imagen+1`,
                        `  alt: Imagen panorámica de prueba 1`,
                        `  caption: Pie editorial de prueba 1.`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/1200x675?text=Imagen+2`,
                        `  alt: Imagen panorámica de prueba 2`,
                        `  caption: Pie editorial de prueba 2.`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        ``,
                        `- image: https://placehold.co/1200x675?text=Imagen+3`,
                        `  alt: Imagen panorámica de prueba 3`,
                        `  caption: Pie editorial de prueba 3.`,
                        `  credit: Fuente autorizada / licencia editorial`,
                        `:::`
                      ].join("\\n")
                    };

                    document.querySelectorAll("[data-template]").forEach((button) => {
                      button.addEventListener("click", () => {
                        markdownOutput.value = templates[button.dataset.template] || "";
                        markdownOutput.focus();
                        copyStatus.textContent = "";
                      });
                    });

                    document.getElementById("copyMarkdownButton").addEventListener("click", async () => {
                      markdownOutput.focus();
                      markdownOutput.select();
                      try {
                        await navigator.clipboard.writeText(markdownOutput.value);
                        copyStatus.textContent = "Markdown copiado.";
                      } catch (error) {
                        document.execCommand("copy");
                        copyStatus.textContent = "Markdown seleccionado y copiado.";
                      }
                    });

                    markdownOutput.value = templates.posterGrid;
                  </script>
                </body>
                </html>
                """);
    }
}
