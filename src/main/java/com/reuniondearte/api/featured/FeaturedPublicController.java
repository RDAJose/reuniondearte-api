package com.reuniondearte.api.featured;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/featured")
public class FeaturedPublicController {
    private final FeaturedSlotRepository featuredSlots;

    public FeaturedPublicController(FeaturedSlotRepository featuredSlots) {
        this.featuredSlots = featuredSlots;
    }

    @GetMapping
    public List<FeaturedResponse> listFeaturedArticles() {
        return featuredSlots.findVisibleFeaturedSlots()
                .stream()
                .map(FeaturedResponse::from)
                .toList();
    }
}

