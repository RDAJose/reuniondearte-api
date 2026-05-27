package com.reuniondearte.api.redirect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "redirects")
public class Redirect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String sourcePath;

    @Column(nullable = false, length = 500)
    private String targetPath;

    private Integer statusCode;
    private Boolean active;

    @Column(columnDefinition = "TEXT")
    private String note;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

