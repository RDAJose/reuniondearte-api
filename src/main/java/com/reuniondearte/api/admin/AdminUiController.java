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
                            <button type="button" id="draftButton" class="secondary">Volver a draft</button>
                          </div>
                        </form>
                        <div class="panel">
                          <h3>Imagen principal</h3>
                          <form id="coverForm">
                            <label for="coverFile">File</label>
                            <input id="coverFile" name="file" type="file" accept="image/jpeg,image/png,image/webp">
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
                            <div class="toolbar" style="margin-top: 14px;">
                              <button type="submit">Subir cover</button>
                            </div>
                          </form>
                          <h3 style="margin-top: 18px;">API publica</h3>
                          <a id="publicApiLink" class="public-link" href="#" target="_blank" rel="noreferrer"></a>
                        </div>
                      </div>
                    </section>
                  </main>
                  <script>
                    const state = { status: "draft", articles: [], current: null, categories: [] };
                    const list = document.getElementById("articleList");
                    const message = document.getElementById("message");
                    const editor = document.getElementById("editor");
                    const articleForm = document.getElementById("articleForm");
                    const coverForm = document.getElementById("coverForm");
                    const categorySelect = document.getElementById("category");

                    document.querySelectorAll("[data-status]").forEach((button) => {
                      button.addEventListener("click", () => {
                        state.status = button.dataset.status;
                        document.querySelectorAll("[data-status]").forEach((item) => item.classList.toggle("active", item === button));
                        loadArticles();
                      });
                    });

                    document.getElementById("refreshButton").addEventListener("click", loadArticles);
                    document.getElementById("publishButton").addEventListener("click", () => changeStatus("publish"));
                    document.getElementById("draftButton").addEventListener("click", () => changeStatus("draft"));
                    articleForm.addEventListener("submit", saveArticle);
                    coverForm.addEventListener("submit", uploadCover);

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
                      state.categories = await api("/api/categories");
                      categorySelect.innerHTML = state.categories
                        .map((category) => `<option value="${escapeHtml(category.slug)}">${escapeHtml(category.name)} (${escapeHtml(category.slug)})</option>`)
                        .join("");
                    }

                    async function loadArticles() {
                      setMessage(`Cargando ${state.status}...`);
                      editor.hidden = true;
                      state.current = null;
                      try {
                        state.articles = await api(`/api/admin/articles?status=${encodeURIComponent(state.status)}`);
                        renderList();
                        setMessage(state.articles.length ? "Selecciona un articulo." : "No hay articulos en este estado.");
                      } catch (error) {
                        setMessage(`Error cargando articulos: ${error.message}`);
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
                      });
                    }

                    async function openArticle(id) {
                      setMessage("Abriendo articulo...");
                      try {
                        const article = await api(`/api/admin/articles/${id}`);
                        state.current = article;
                        list.querySelectorAll("[data-id]").forEach((button) => button.classList.toggle("active", button.dataset.id === String(id)));
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
                      document.getElementById("status").value = article.status || "draft";
                      document.getElementById("canonicalUrl").value = article.canonical_url || "";
                      document.getElementById("metaTitle").value = article.meta_title || "";
                      document.getElementById("metaDescription").value = article.meta_description || "";
                      document.getElementById("noindex").checked = Boolean(article.noindex);
                      const publicPath = `/api/articles/${encodeURIComponent(article.slug || "")}`;
                      const publicLink = document.getElementById("publicApiLink");
                      publicLink.href = publicPath;
                      publicLink.textContent = `${window.location.origin}${publicPath}`;
                    }

                    async function saveArticle(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      setMessage("Guardando...");
                      const body = {
                        title: document.getElementById("title").value.trim(),
                        slug: document.getElementById("slug").value.trim(),
                        excerpt: document.getElementById("excerpt").value,
                        content_markdown: document.getElementById("contentMarkdown").value,
                        category: document.getElementById("category").value,
                        status: document.getElementById("status").value,
                        canonical_url: emptyToNull(document.getElementById("canonicalUrl").value),
                        meta_title: emptyToNull(document.getElementById("metaTitle").value),
                        meta_description: emptyToNull(document.getElementById("metaDescription").value),
                        noindex: document.getElementById("noindex").checked,
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

                    async function uploadCover(event) {
                      event.preventDefault();
                      if (!state.current) return;
                      const file = document.getElementById("coverFile").files[0];
                      const altText = document.getElementById("altText").value.trim();
                      if (!file || !altText) {
                        setMessage("Selecciona una imagen y escribe altText.");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", file);
                      formData.append("altText", altText);
                      appendIfPresent(formData, "caption", document.getElementById("caption").value);
                      appendIfPresent(formData, "credit", document.getElementById("credit").value);
                      appendIfPresent(formData, "sourceUrl", document.getElementById("sourceUrl").value);
                      appendIfPresent(formData, "rightsNotes", document.getElementById("rightsNotes").value);
                      setMessage("Subiendo cover...");
                      try {
                        const result = await api(`/api/admin/articles/${state.current.id}/cover`, { method: "POST", body: formData });
                        setMessage(`Cover guardada: ${result.coverImage}`);
                        coverForm.reset();
                      } catch (error) {
                        setMessage(`Error subiendo cover: ${error.message}`);
                      }
                    }

                    function appendIfPresent(formData, key, value) {
                      if (value && value.trim()) {
                        formData.append(key, value.trim());
                      }
                    }

                    function emptyToNull(value) {
                      return value && value.trim() ? value.trim() : null;
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

                    loadCategories()
                      .then(loadArticles)
                      .catch((error) => setMessage(`Error inicializando admin: ${error.message}`));
                  </script>
                </body>
                </html>
                """;
    }
}
