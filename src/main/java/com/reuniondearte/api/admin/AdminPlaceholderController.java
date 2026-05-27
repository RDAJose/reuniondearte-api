package com.reuniondearte.api.admin;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminPlaceholderController {
    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("status", "admin-api-reserved");
    }
}

