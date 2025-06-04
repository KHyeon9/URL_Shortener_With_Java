package com.hyeon.url_shortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "short_urls")
@Entity
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String shortKey;
    @Column(nullable = false)
    private String originalUrl;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private boolean isPrivate;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiredAt;

    private Long clickCount;
}
