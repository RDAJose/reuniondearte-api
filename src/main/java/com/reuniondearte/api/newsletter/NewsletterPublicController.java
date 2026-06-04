package com.reuniondearte.api.newsletter;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterPublicController {
    private final NewsletterService newsletter;

    public NewsletterPublicController(NewsletterService newsletter) {
        this.newsletter = newsletter;
    }

    @PostMapping("/subscribe")
    public NewsletterSubscribeResponse subscribe(@Valid @RequestBody NewsletterSubscribeRequest request) {
        return newsletter.subscribe(request);
    }

    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public String confirm(@RequestParam String token) {
        boolean confirmed = newsletter.confirm(token);
        return simplePage(
                confirmed ? "Suscripcion confirmada" : "Enlace no valido",
                confirmed ? "Tu alta en la newsletter de Reunion de Arte ha quedado confirmada." : "El enlace de confirmacion no es valido o ya fue utilizado."
        );
    }

    @GetMapping(value = "/unsubscribe", produces = MediaType.TEXT_HTML_VALUE)
    public String unsubscribe(@RequestParam String token) {
        boolean unsubscribed = newsletter.unsubscribe(token);
        return simplePage(
                unsubscribed ? "Baja confirmada" : "Enlace no valido",
                unsubscribed ? "Tu baja de la newsletter de Reunion de Arte ha quedado registrada." : "El enlace de baja no es valido o ya fue utilizado."
        );
    }

    private String simplePage(String title, String message) {
        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <style>
                    body { margin: 0; min-height: 100vh; display: grid; place-items: center; font-family: system-ui, -apple-system, "Segoe UI", sans-serif; color: #1c1917; background: #fffdf8; }
                    main { width: min(92vw, 560px); border: 1px solid #d6d3d1; background: #fff; padding: 28px; }
                    h1 { margin: 0 0 10px; font-size: 24px; }
                    p { margin: 0; color: #57534e; line-height: 1.5; }
                  </style>
                </head>
                <body><main><h1>%s</h1><p>%s</p></main></body>
                </html>
                """.formatted(escape(title), escape(title), escape(message));
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
