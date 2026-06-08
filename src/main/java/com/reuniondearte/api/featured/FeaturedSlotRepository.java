package com.reuniondearte.api.featured;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeaturedSlotRepository extends JpaRepository<FeaturedSlot, Long> {
    @EntityGraph(attributePaths = {"article", "article.author", "article.author.avatarMedia", "article.articleAuthors", "article.articleAuthors.author", "article.articleAuthors.author.avatarMedia", "article.primaryCategory", "article.coverMedia"})
    @Query("""
            select slot
            from FeaturedSlot slot
            where slot.active = true
              and slot.article.status = com.reuniondearte.api.article.ArticleStatus.published
              and (slot.startsAt is null or slot.startsAt <= current_timestamp)
              and (slot.endsAt is null or slot.endsAt >= current_timestamp)
            order by slot.slotKey asc, slot.sortOrder asc
            """)
    List<FeaturedSlot> findVisibleFeaturedSlots();
}
