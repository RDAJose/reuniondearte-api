package com.reuniondearte.api.health;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping({"/api/health", "/health"})
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "reuniondearte-api",
                "time", OffsetDateTime.now().toString()
        );
    }
}
