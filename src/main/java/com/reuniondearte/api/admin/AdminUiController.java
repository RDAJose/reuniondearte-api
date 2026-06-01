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
                    .grid { display: grid; grid-template-columns: minmax(0, 1fr) minmax(260px, 340px); gap: 18px; align-items: start; }
                    .panel { border: 1px solid var(--line); background: #fff; padding: 14px; }
                    .panel h2, .panel h3 { margin: 0 0 8px; font-size: 16px; }
                    .meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
                    .message { min-height: 24px; margin: 8px 0 12px; color: var(--muted); font-size: 14px; }
                    .public-link { word-break: break-all; color: #1d4ed8; font-size: 14px; }
                    .image-status { display: inline-flex; margin: 4px 0 12px; border: 1px solid var(--line); background: var(--soft); padding: 6px 8px; font-size: 13px; font-weight: 700; }
                    .image-status.assigned { border-color: #166534; color: #166534; background: #f0fdf4; }
                    .image-preview { width: 100%; max-height: 220px; object-fit: cover; border: 1px solid var(--line); background: var(--soft); }
                    .data-list { display: grid; gap: 8px; margin: 8px 0 14px; font-size: 13px; }
                    .data-list div { display: grid; gap: 3px; }
                    .data-list dt { color: var(--muted); font-weight: 700; text-transform: uppercase; font-size: 11px; }
                    .data-list dd { margin: 0; word-break: break-word; }
                    .notice { border-left: 3px solid #92400e; background: #fffbeb; color: #713f12; padding: 10px 12px; font-size: 13px; line-height: 1.45; }
                    .warning-list { display: grid; gap: 6px; margin: 10px 0 12px; padding: 10px 12px; border: 1px solid #f59e0b; background: #fffbeb; color: #713f12; font-size: 13px; line-height: 1.4; }
                    .hint { color: var(--muted); font-size: 12px; line-height: 1.45; }
                    .snippet { width: 100%; min-height: 86px; font-family: ui-monospace, SFMono-Regular, Consolas, "Liberation Mono", monospace; font-size: 12px; }
                    .body-image { border-top: 1px solid var(--line); margin-top: 12px; padding-top: 12px; }
                    .comment-panel { margin-bottom: 18px; }
                    .comment-list { display: grid; gap: 10px; }
                    .comment-card { border: 1px solid var(--line); background: #fff; padding: 12px; }
                    .comment-card header { position: static; border: 0; padding: 0; background: transparent; }
                    .comment-card strong { display: block; font-size: 14px; }
                    .comment-card small { display: block; margin-top: 3px; color: var(--muted); }
                    .comment-card p { white-space: pre-wrap; line-height: 1.45; margin: 10px 0; }
                    .empty { color: var(--muted); padding: 20px 0; }
                    @media (max-width: 880px) {
                      main { grid-template-columns: 1fr; }
                      aside { border-right: 0; border-bottom: 1px solid var(--line); }
                      .grid { grid-template-columns: 1fr; }
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
                      </div>
                      <div id="articleList" class="article-list"></div>
                    </aside>
                    <section>
                      <p id="message" class="message">Selecciona un articulo.</p>
                      <div class="panel comment-panel">
                        <div class="toolbar">
                          <h2 style="margin:0; font-size:16px; flex:1;">Comentarios pendientes</h2>
                          <button type="button" id="refreshCommentsButton" class="secondary">Recargar</button>
                        </div>
                        <div id="pendingComments" class="comment-list"></div>
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
                        <div class="panel">
                          <h3>Imagen principal</h3>
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
                          <h3 style="margin-top: 18px;">Multimedia del cuerpo</h3>
                          <p class="hint">Sube imagenes propias, press kit autorizado o material con derechos claros. Las imagenes quitadas del articulo no se borran automaticamente de R2.</p>
                          <form id="bodyImageForm">
                            <label for="bodyImageFile">Imagenes</label>
                            <input id="bodyImageFile" name="files" type="file" accept="image/jpeg,image/png,image/webp" multiple>
                            <p id="bodyFileHint" class="hint"></p>
                            <div class="toolbar" style="margin-top: 14px;">
                              <button type="submit">Subir imagenes de cuerpo</button>
                            </div>
                          </form>
                          <form id="bodyImportForm">
                            <label for="bodyImportUrl">Importar imagen de cuerpo desde URL</label>
                            <input id="bodyImportUrl" name="imageUrl" type="url" placeholder="https://...">
                            <div class="toolbar" style="margin-top: 14px;">
                              <button type="submit">Importar imagen de cuerpo</button>
                            </div>
                          </form>
                          <form id="bodyMediaForm">
                            <label for="bodyMediaFile">Audio/video propio</label>
                            <input id="bodyMediaFile" name="file" type="file" accept="audio/mpeg,audio/wav,audio/ogg,audio/mp4,video/mp4,video/webm">
                            <p id="bodyMediaFileHint" class="hint">Audio maximo 50 MB. Video maximo 150 MB.</p>
                            <div class="toolbar" style="margin-top: 14px;">
                              <button type="submit" class="secondary">Subir audio/video</button>
                            </div>
                          </form>
                          <h3 style="margin-top: 18px;">Galeria y embeds</h3>
                          <div class="toolbar">
                            <button type="button" id="insertGalleryButton" class="secondary">Insertar galeria seleccionada</button>
                            <button type="button" id="copyGalleryButton" class="secondary">Copiar galeria</button>
                          </div>
                          <form id="embedForm">
                            <label for="embedUrl">YouTube, Vimeo, Spotify o SoundCloud</label>
                            <input id="embedUrl" name="embedUrl" type="url" placeholder="https://...">
                            <p class="hint">No pegues iframes ni scripts. Usa enlaces limpios.</p>
                            <div class="toolbar" style="margin-top: 14px;">
                              <button type="submit" class="secondary">Insertar enlace</button>
                            </div>
                          </form>
                          <div id="bodyImages"></div>
                          <h3 style="margin-top: 18px;">API publica</h3>
                          <a id="publicApiLink" class="public-link" href="#" target="_blank" rel="noreferrer"></a>
                        </div>
                      </div>
                    </section>
                  </main>
                  <script>
                    const state = { status: "draft", articles: [], current: null, categories: [], comments: [] };
                    const byId = (id) => document.getElementById(id);
                    const list = byId("articleList");
                    const message = byId("message");
                    const pendingComments = byId("pendingComments");
                    const editor = byId("editor");
                    const articleForm = byId("articleForm");
                    const coverForm = byId("coverForm");
                    const coverMetadataForm = byId("coverMetadataForm");
                    const coverImportForm = byId("coverImportForm");
                    const bodyImageForm = byId("bodyImageForm");
                    const bodyImportForm = byId("bodyImportForm");
                    const bodyMediaForm = byId("bodyMediaForm");
                    const embedForm = byId("embedForm");
                    const categorySelect = byId("category");

                    document.querySelectorAll("[data-status]").forEach((button) => {
                      button.addEventListener("click", () => {
                        state.status = button.dataset.status;
                        document.querySelectorAll("[data-status]").forEach((item) => item.classList.toggle("active", item === button));
                        loadArticles();
                      });
                    });

                    on("refreshButton", "click", loadArticles);
                    on("refreshCommentsButton", "click", loadPendingComments);
                    on("publishButton", "click", () => changeStatus("publish"));
                    on("draftButton", "click", () => changeStatus("draft"));
                    on("deleteArticleButton", "click", deleteArticle);
                    on("removeCoverButton", "click", removeCover);
                    articleForm?.addEventListener("submit", saveArticle);
                    coverForm?.addEventListener("submit", uploadCover);
                    coverMetadataForm?.addEventListener("submit", saveCoverMetadata);
                    coverImportForm?.addEventListener("submit", importCover);
                    bodyImageForm?.addEventListener("submit", uploadBodyImage);
                    bodyImportForm?.addEventListener("submit", importBodyImage);
                    bodyMediaForm?.addEventListener("submit", uploadBodyMedia);
                    embedForm?.addEventListener("submit", insertEmbed);
                    on("insertGalleryButton", "click", () => insertTextAtCursor(buildGallerySnippet()));
                    on("copyGalleryButton", "click", copyGallery);
                    on("coverFile", "change", (event) => showFileHint(event, "coverFileHint"));
                    on("bodyImageFile", "change", (event) => showFileHint(event, "bodyFileHint"));
                    on("bodyMediaFile", "change", (event) => showFileHint(event, "bodyMediaFileHint"));

                    function on(id, eventName, handler) {
                      const element = byId(id);
                      if (element) {
                        element.addEventListener(eventName, handler);
                      }
                    }

                    async function api(path, options = {}) {
                      const response = await fetch(path, {
                        headers: { Accept: "application/json", ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }) },
                        ...options,
                      });
                      if (!response.ok) {
                        const text = await response.text();
                        throw new Error(text || `${response.status} ${response.statusText}`);
                      }
                      if (response.status === 204) {
                        return null;
                      }
                      return response.json();
                    }

                    async function loadCategories() {
                      if (!categorySelect) return;
                      state.categories = await api("/api/categories");
                      categorySelect.innerHTML = state.categories
                        .map((category) => `<option value="${escapeHtml(category.slug)}">${escapeHtml(category.name)} (${escapeHtml(category.slug)})</option>`)
                        .join("");
                    }

                    async function loadArticles() {
                      if (!list) return;
                      setMessage(`Cargando ${state.status}...`);
                      if (editor) editor.hidden = true;
                      state.current = null;
                      try {
                        state.articles = await api(`/api/admin/articles?status=${encodeURIComponent(state.status)}`);
                        renderList();
                        setMessage(state.articles.length ? "Selecciona un articulo." : "No hay articulos en este estado.");
                      } catch (error) {
                        setMessage(`Error cargando articulos: ${error.message}`);
                      }
                    }

                    async function loadPendingComments() {
                      if (!pendingComments) return;
                      pendingComments.innerHTML = `<p class="empty">Cargando comentarios...</p>`;
                      try {
                        state.comments = await api("/api/admin/comments?status=PENDING");
                        renderPendingComments();
                      } catch (error) {
                        pendingComments.innerHTML = `<p class="empty">Error cargando comentarios: ${escapeHtml(error.message)}</p>`;
                      }
                    }

                    function renderPendingComments() {
                      if (!pendingComments) return;
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
                      if (!card) return;
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
                      if (!list) return;
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
                      });
                    }

                    async function openArticle(id) {
                      setMessage("Abriendo articulo...");
                      try {
                        const article = await api(`/api/admin/articles/${id}`);
                        state.current = article;
                        list?.querySelectorAll("[data-id]").forEach((button) => button.classList.toggle("active", button.dataset.id === String(id)));
                        fillForm(article);
                        if (editor) editor.hidden = false;
                        setMessage(`Articulo #${article.id} abierto.`);
                      } catch (error) {
                        setMessage(`Error abriendo articulo: ${error.message}`);
                      }
                    }

                    function fillForm(article) {
                      setText("editorTitle", `Articulo #${article.id}`);
                      setValue("title", article.title || "");
                      setValue("slug", article.slug || "");
                      setValue("excerpt", article.excerpt || "");
                      setValue("contentMarkdown", article.content_markdown || "");
                      setValue("category", article.category || "cultura");
                      setValue("status", article.status || "draft");
                      setValue("canonicalUrl", article.canonical_url || "");
                      setValue("metaTitle", article.meta_title || "");
                      setValue("metaDescription", article.meta_description || "");
                      setChecked("noindex", Boolean(article.noindex));
                      const publicPath = `/api/articles/${encodeURIComponent(article.slug || "")}`;
                      const publicLink = byId("publicApiLink");
                      if (publicLink) {
                        publicLink.href = publicPath;
                        publicLink.textContent = `${window.location.origin}${publicPath}`;
                      }
                      fillImageMetadata(article.cover);
                      renderCoverWarnings(article.cover);
                      renderCurrentCover(article.cover);
                      renderBodyImages(article.bodyImages || []);
                      setDisabled("removeCoverButton", !article.cover);
                      setDisabled("deleteArticleButton", article.status === "published");
                    }

                    function fillImageMetadata(cover) {
                      setValue("altText", cover?.coverAlt || "");
                      setValue("caption", cover?.coverCaption || "");
                      setValue("credit", cover?.coverCredit || "");
                      setValue("sourceUrl", cover?.sourceUrl || "");
                      setValue("rightsNotes", cover?.rightsNotes || "");
                      setValue("coverFile", "");
                      setValue("coverImportUrl", "");
                      setText("coverFileHint", "");
                      setValue("bodyImageFile", "");
                      setValue("bodyImportUrl", "");
                      setText("bodyFileHint", "");
                      setValue("bodyMediaFile", "");
                    }

                    function renderCurrentCover(cover) {
                      const status = byId("coverStatus");
                      const container = byId("currentCover");
                      if (!status || !container) return;
                      status.textContent = cover ? "Imagen principal asignada" : "Sin imagen principal";
                      status.classList.toggle("assigned", Boolean(cover));
                      if (!cover) {
                        container.innerHTML = `<p class="empty">Este articulo todavia no tiene imagen principal. Puedes subir una nueva o importarla desde URL.</p>`;
                        return;
                      }
                      container.innerHTML = `
                        <img class="image-preview" src="${escapeAttribute(cover.coverImage)}" alt="${escapeAttribute(cover.coverAlt || "")}">
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
                      const container = byId("coverWarnings");
                      if (!container) return;
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
                      const container = byId("bodyImages");
                      if (!container) return;
                      if (!images.length) {
                        container.innerHTML = `<p class="empty">Sin multimedia del cuerpo asociada.</p>`;
                        return;
                      }
                      container.innerHTML = images.map((image, index) => `
                        <div class="body-image">
                          <label style="display:flex; gap:8px; align-items:center; margin:0 0 8px; text-transform:none; font-size:14px; color:var(--ink);">
                            <input type="checkbox" data-gallery="${image.mediaAssetId}" style="width:auto; margin:0;" ${image.mediaType === "image" ? "" : "disabled"}>
                            <strong>${escapeHtml(mediaLabel(image, index))}</strong>
                          </label>
                          ${mediaPreview(image)}
                          <dl class="data-list">
                            <div><dt>URL publica</dt><dd><a class="public-link" href="${escapeAttribute(image.publicUrl)}" target="_blank" rel="noreferrer">${escapeHtml(image.publicUrl)}</a></dd></div>
                            <div><dt>Alt text</dt><dd>${escapeHtml(image.altText || "-")}</dd></div>
                            <div><dt>Caption</dt><dd>${escapeHtml(image.caption || "-")}</dd></div>
                            <div><dt>Credit</dt><dd>${escapeHtml(image.credit || "-")}</dd></div>
                            <div><dt>Source URL</dt><dd>${linkOrDash(image.sourceUrl)}</dd></div>
                            <div><dt>Rights notes</dt><dd>${escapeHtml(image.rightsNotes || "-")}</dd></div>
                            <div><dt>Peso</dt><dd>${formatBytes(image.size_bytes)}${imageSizeLabel(image)}</dd></div>
                          </dl>
                          <label for="snippet-${image.mediaAssetId}">Snippet Markdown</label>
                          <textarea id="snippet-${image.mediaAssetId}" class="snippet" readonly>${escapeHtml(image.markdownSnippet || "")}</textarea>
                          <div class="toolbar">
                            <button type="button" class="secondary" data-insert="${image.mediaAssetId}">Insertar snippet</button>
                            <button type="button" class="secondary" data-copy="${image.mediaAssetId}">Copiar snippet</button>
                            <button type="button" class="danger" data-remove-media="${image.mediaAssetId}">Quitar del articulo</button>
                          </div>
                        </div>
                      `).join("");
                      container.querySelectorAll("[data-insert]").forEach((button) => {
                        button.addEventListener("click", () => {
                          const textarea = byId(`snippet-${button.dataset.insert}`);
                          insertTextAtCursor(textarea ? textarea.value : "");
                        });
                      });
                      container.querySelectorAll("[data-copy]").forEach((button) => {
                        button.addEventListener("click", async () => {
                          const textarea = byId(`snippet-${button.dataset.copy}`);
                          if (!textarea) return;
                          await copyText(textarea.value);
                          setMessage("Snippet Markdown copiado.");
                        });
                      });
                      container.querySelectorAll("[data-remove-media]").forEach((button) => {
                        button.addEventListener("click", () => removeBodyMedia(button.dataset.removeMedia));
                      });
                    }

                    function mediaLabel(media, index) {
                      const type = media.mediaType === "audio" ? "Audio" : media.mediaType === "video" ? "Video" : "Imagen";
                      return `${type} de cuerpo ${index + 1}`;
                    }

                    function mediaPreview(media) {
                      if (media.mediaType === "audio") {
                        return `<audio controls src="${escapeAttribute(media.publicUrl)}" style="width:100%;"></audio>`;
                      }
                      if (media.mediaType === "video") {
                        return `<video controls src="${escapeAttribute(media.publicUrl)}" style="width:100%; max-height:240px; background:#111;"></video>`;
                      }
                      return `<img class="image-preview" src="${escapeAttribute(media.publicUrl)}" alt="${escapeAttribute(media.altText || "")}">`;
                    }

                    async function removeBodyMedia(mediaAssetId) {
                      if (!state.current) return;
                      const confirmed = confirm("Quitar este media del articulo. El fichero de storage/R2 se conserva. Continuar?");
                      if (!confirmed) return;
                      setMessage("Quitando media del articulo...");
                      try {
                        await api(`/api/admin/articles/${state.current.id}/body-media/${mediaAssetId}`, { method: "DELETE" });
                        await openArticle(state.current.id);
                        setMessage("Media quitado del articulo. El fichero no se ha borrado.");
                      } catch (error) {
                        setMessage(`Error quitando media: ${error.message}`);
                      }
                    }

                    function selectedGalleryImages() {
                      if (!state.current || !state.current.bodyImages) return [];
                      const selectedIds = [...document.querySelectorAll("[data-gallery]:checked")].map((item) => Number(item.dataset.gallery));
                      return state.current.bodyImages.filter((image) => selectedIds.includes(Number(image.mediaAssetId)) && image.mediaType === "image");
                    }

                    function buildGallerySnippet() {
                      const images = selectedGalleryImages();
                      if (!images.length) {
                        setMessage("Selecciona al menos una imagen para la galeria.");
                        return "";
                      }
                      const lines = images.map((image) => `![${markdownAlt(image.altText)}](${safeMarkdownUrl(image.publicUrl)})`);
                      return `:::gallery\n${lines.join("\n")}\n:::`;
                    }

                    async function copyGallery() {
                      const snippet = buildGallerySnippet();
                      if (!snippet) return;
                      await copyText(snippet);
                      setMessage("Galeria copiada.");
                    }

                    async function saveArticle(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      setMessage("Guardando...");
                      const body = {
                        title: valueOf("title").trim(),
                        slug: valueOf("slug").trim(),
                        excerpt: valueOf("excerpt"),
                        content_markdown: valueOf("contentMarkdown"),
                        category: valueOf("category"),
                        status: valueOf("status"),
                        canonical_url: emptyToNull(valueOf("canonicalUrl")),
                        meta_title: emptyToNull(valueOf("metaTitle")),
                        meta_description: emptyToNull(valueOf("metaDescription")),
                        noindex: Boolean(byId("noindex")?.checked),
                      };
                      try {
                        const saved = await api(`/api/admin/articles/${state.current.id}`, { method: "PUT", body: JSON.stringify(body) });
                        state.current = saved;
                        fillForm(saved);
                        await loadArticles();
                        await openArticle(saved.id);
                        setMessage("Cambios guardados.");
                      } catch (error) {
                        setMessage(`Error guardando: ${error.message}`);
                      }
                    }

                    async function changeStatus(action) {
                      if (!state.current) return;
                      if (action === "draft" && state.current.status === "published") {
                        const confirmed = confirm("Mover este articulo publicado a borrador lo retirara de la API publica. No borra imagenes ni ficheros. ¿Continuar?");
                        if (!confirmed) return;
                      }
                      setMessage(action === "publish" ? "Publicando..." : "Moviendo a draft...");
                      try {
                        const updated = await api(`/api/admin/articles/${state.current.id}/${action}`, { method: "PATCH" });
                        state.current = updated;
                        state.status = updated.status;
                        document.querySelectorAll("[data-status]").forEach((item) => item.classList.toggle("active", item.dataset.status === state.status));
                        await loadArticles();
                        await openArticle(updated.id);
                        setMessage(action === "publish" ? "Articulo publicado." : "Articulo en draft.");
                      } catch (error) {
                        setMessage(`Error cambiando estado: ${error.message}`);
                      }
                    }

                    async function deleteArticle() {
                      if (!state.current) return;
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
                        await api(`/api/admin/articles/${state.current.id}`, { method: "DELETE" });
                        editor.hidden = true;
                        state.current = null;
                        await loadArticles();
                        setMessage("Articulo eliminado. Los ficheros de media no se han borrado fisicamente.");
                      } catch (error) {
                        setMessage(`Error eliminando articulo: ${error.message}`);
                      }
                    }

                    async function uploadCover(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const file = byId("coverFile")?.files?.[0];
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
                        const result = await api(`/api/admin/articles/${state.current.id}/cover`, { method: "POST", body: formData });
                        setMessage(`Cover guardada: ${result.coverImage}`);
                        await openArticle(state.current.id);
                      } catch (error) {
                        setMessage(`Error subiendo cover: ${error.message}`);
                      }
                    }

                    async function saveCoverMetadata(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      if (!state.current.cover) {
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
                        await api(`/api/admin/articles/${state.current.id}/cover/metadata`, { method: "PATCH", body: JSON.stringify(metadata) });
                        await openArticle(state.current.id);
                        setMessage("Datos de imagen guardados.");
                      } catch (error) {
                        setMessage(`Error guardando datos de imagen: ${error.message}`);
                      }
                    }

                    async function removeCover() {
                      if (!state.current || !state.current.cover) return;
                      const confirmed = confirm("Quitar la imagen principal desasignara la portada del articulo. No borrara el fichero fisico ni el media asset. ¿Continuar?");
                      if (!confirmed) return;
                      setMessage("Quitando imagen principal...");
                      try {
                        await api(`/api/admin/articles/${state.current.id}/cover`, { method: "DELETE" });
                        await openArticle(state.current.id);
                        setMessage("Imagen principal quitada. El fichero de storage se conserva.");
                      } catch (error) {
                        setMessage(`Error quitando imagen principal: ${error.message}`);
                      }
                    }

                    async function importCover(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const body = importPayload("coverImportUrl");
                      if (!body) return;
                      setMessage("Importando cover...");
                      try {
                        const result = await api(`/api/admin/articles/${state.current.id}/cover/import`, { method: "POST", body: JSON.stringify(body) });
                        setMessage(`Cover importada: ${result.coverImage}`);
                        await openArticle(state.current.id);
                      } catch (error) {
                        setMessage(`Error importando cover: ${error.message}`);
                      }
                    }

                    async function uploadBodyImage(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const files = [...(byId("bodyImageFile")?.files || [])];
                      const metadata = imageMetadata();
                      if (!files.length || !metadata.altText) {
                        setMessage("Selecciona una o varias imagenes de cuerpo y escribe altText.");
                        return;
                      }
                      const formData = new FormData();
                      files.forEach((file) => formData.append("files", file));
                      appendMetadata(formData, metadata);
                      setMessage("Subiendo imagenes de cuerpo...");
                      try {
                        const result = await api(`/api/admin/articles/${state.current.id}/body-images/batch`, { method: "POST", body: formData });
                        setMessage(`${result.length} imagen(es) de cuerpo guardadas.`);
                        await openArticle(state.current.id);
                      } catch (error) {
                        setMessage(`Error subiendo imagenes de cuerpo: ${error.message}`);
                      }
                    }

                    async function importBodyImage(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const body = importPayload("bodyImportUrl");
                      if (!body) return;
                      setMessage("Importando imagen de cuerpo...");
                      try {
                        const result = await api(`/api/admin/articles/${state.current.id}/body-images/import`, { method: "POST", body: JSON.stringify(body) });
                        setMessage(`Imagen de cuerpo importada. Snippet disponible para copiar.`);
                        await openArticle(state.current.id);
                      } catch (error) {
                        setMessage(`Error importando imagen de cuerpo: ${error.message}`);
                      }
                    }

                    async function uploadBodyMedia(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const file = byId("bodyMediaFile")?.files?.[0];
                      const metadata = imageMetadata();
                      if (!file) {
                        setMessage("Selecciona un audio o video.");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", file);
                      appendMetadata(formData, metadata);
                      setMessage("Subiendo audio/video...");
                      try {
                        const result = await api(`/api/admin/articles/${state.current.id}/body-media`, { method: "POST", body: formData });
                        setMessage(`Media guardado: ${result.publicUrl}`);
                        await openArticle(state.current.id);
                      } catch (error) {
                        setMessage(`Error subiendo audio/video: ${error.message}`);
                      }
                    }

                    async function insertEmbed(event) {
                      event.preventDefault();
                      const input = byId("embedUrl");
                      if (!input) return;
                      const cleaned = cleanEmbedUrl(input.value);
                      if (!cleaned) {
                        setMessage("Pega un enlace valido de YouTube, Vimeo, Spotify o SoundCloud.");
                        return;
                      }
                      insertTextAtCursor(cleaned);
                      input.value = "";
                      setMessage("Enlace insertado en Markdown.");
                    }

                    function imageMetadata() {
                      return {
                        altText: (byId("altText")?.value || "").trim(),
                        caption: (byId("caption")?.value || "").trim(),
                        credit: (byId("credit")?.value || "").trim(),
                        sourceUrl: (byId("sourceUrl")?.value || "").trim(),
                        rightsNotes: (byId("rightsNotes")?.value || "").trim(),
                      };
                    }

                    function importPayload(inputId) {
                      const imageUrl = (byId(inputId)?.value || "").trim();
                      const metadata = imageMetadata();
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

                    function appendIfPresent(formData, key, value) {
                      if (value && value.trim()) {
                        formData.append(key, value.trim());
                      }
                    }

                    function emptyToNull(value) {
                      return value && value.trim() ? value.trim() : null;
                    }

                    function valueOf(id) {
                      return byId(id)?.value || "";
                    }

                    function showFileHint(event, targetId) {
                      const files = [...(event.target?.files || [])];
                      const target = byId(targetId);
                      if (!target) return;
                      if (!files.length) {
                        target.textContent = "";
                        return;
                      }
                      const warnings = [];
                      const total = files.reduce((sum, file) => sum + file.size, 0);
                      if (files.some((file) => file.size > 8 * 1024 * 1024 && file.type.startsWith("image/"))) warnings.push("alguna imagen supera el limite backend de 8 MB");
                      if (files.some((file) => file.size > 50 * 1024 * 1024 && file.type.startsWith("audio/"))) warnings.push("algun audio supera 50 MB");
                      if (files.some((file) => file.size > 150 * 1024 * 1024 && file.type.startsWith("video/"))) warnings.push("algun video supera 150 MB");
                      if (files.some((file) => file.size > 400 * 1024 && file.type.startsWith("image/"))) warnings.push("para imagenes, intenta optimizar por debajo de 400 KB si es viable");
                      const label = files.length === 1 ? files[0].name : `${files.length} archivos`;
                      target.textContent = `${label} - ${formatBytes(total)}${warnings.length ? " - Aviso: " + warnings.join("; ") : ""}`;
                    }

                    function insertTextAtCursor(snippet) {
                      if (!snippet) return;
                      const textarea = byId("contentMarkdown");
                      if (!textarea) return;
                      const text = `\n\n${snippet.trim()}\n\n`;
                      const start = typeof textarea.selectionStart === "number" ? textarea.selectionStart : textarea.value.length;
                      const end = typeof textarea.selectionEnd === "number" ? textarea.selectionEnd : textarea.value.length;
                      textarea.value = textarea.value.slice(0, start) + text + textarea.value.slice(end);
                      const cursor = start + text.length;
                      textarea.focus();
                      textarea.setSelectionRange(cursor, cursor);
                      setMessage("Snippet insertado en Content markdown. Guarda el articulo para persistirlo.");
                    }

                    function cleanEmbedUrl(value) {
                      if (!value || /<|>|script|iframe/i.test(value)) return "";
                      let url;
                      try {
                        url = new URL(value.trim());
                      } catch {
                        return "";
                      }
                      if (!["http:", "https:"].includes(url.protocol)) return "";
                      let host = url.hostname.toLowerCase();
                      if (host.startsWith("www.")) host = host.substring(4);
                      const allowedHosts = ["youtube.com", "youtu.be", "vimeo.com", "spotify.com", "open.spotify.com", "soundcloud.com"];
                      if (!allowedHosts.some((allowed) => host === allowed || host.endsWith(`.${allowed}`))) return "";
                      url.hash = "";
                      return url.toString();
                    }

                    function markdownAlt(value) {
                      return String(value || "")
                        .replaceAll("[", "(")
                        .replaceAll("]", ")")
                        .replaceAll("\n", " ")
                        .replaceAll("\r", " ")
                        .trim();
                    }

                    function safeMarkdownUrl(value) {
                      try {
                        const url = new URL(value);
                        return ["http:", "https:"].includes(url.protocol) ? url.toString().replaceAll(")", "%29") : "";
                      } catch {
                        return "";
                      }
                    }

                    async function copyText(value) {
                      if (navigator.clipboard && window.isSecureContext) {
                        await navigator.clipboard.writeText(value);
                        return;
                      }
                      const textarea = document.createElement("textarea");
                      textarea.value = value;
                      textarea.style.position = "fixed";
                      textarea.style.left = "-9999px";
                      document.body.appendChild(textarea);
                      textarea.focus();
                      textarea.select();
                      document.execCommand("copy");
                      textarea.remove();
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
                      if (message) {
                        message.textContent = value;
                      }
                    }

                    function setValue(id, value) {
                      const element = byId(id);
                      if (element) {
                        element.value = value;
                      }
                    }

                    function setText(id, value) {
                      const element = byId(id);
                      if (element) {
                        element.textContent = value;
                      }
                    }

                    function setChecked(id, value) {
                      const element = byId(id);
                      if (element) {
                        element.checked = value;
                      }
                    }

                    function setDisabled(id, value) {
                      const element = byId(id);
                      if (element) {
                        element.disabled = value;
                      }
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

                    async function initAdmin() {
                      try {
                        await loadCategories();
                      } catch (error) {
                        setMessage(`Error cargando categorias: ${error.message}`);
                      }
                      try {
                        await loadArticles();
                      } catch (error) {
                        setMessage(`Error cargando articulos: ${error.message}`);
                      }
                      try {
                        await loadPendingComments();
                      } catch (error) {
                        setMessage(`Error cargando comentarios: ${error.message}`);
                      }
                    }

                    initAdmin().catch((error) => setMessage(`Error inicializando admin: ${error.message}`));
                  </script>
                </body>
                </html>
                """;
    }
}
