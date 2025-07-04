package com.hyeon.url_shortener.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "short_urls")
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "short_urls_id_gen")
    @SequenceGenerator(name = "short_urls_id_gen", sequenceName = "short_urls_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "short_key", nullable = false, length = 10)
    private String shortKey;

    @Column(name = "original_url", nullable = false, length = Integer.MAX_VALUE)
    private String originalUrl;

    @ColumnDefault("false")
    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private com.hyeon.url_shortener.domain.entity.User createdBy;

    @ColumnDefault("0")
    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShortUrl() {}

    private ShortUrl (
            String shortKey,
            String originalUrl,
            Boolean isPrivate,
            Instant expiredAt,
            User createdBy,
            Long clickCount,
            Instant createdAt
    ) {
        this.shortKey = shortKey;
        this.originalUrl = originalUrl;
        this.isPrivate = isPrivate;
        this.expiredAt = expiredAt;
        this.createdBy = createdBy;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
    }

    public static ShortUrl of (
            String shortKey,
            String originalUrl,
            Boolean isPrivate,
            Instant expiredAt,
            User createdBy,
            Long clickCount,
            Instant createdAt
    ) {
        return new ShortUrl(shortKey, originalUrl, isPrivate, expiredAt, createdBy, clickCount, createdAt);
    }

    public static ShortUrl of (
            String shortKey,
            String originalUrl,
            Instant expiredAt,
            Long clickCount,
            Instant createdAt
    ) {
        return new ShortUrl(
                shortKey,
                originalUrl,
                false,
                expiredAt,
                null,
                clickCount,
                createdAt)
            ;
    }
}