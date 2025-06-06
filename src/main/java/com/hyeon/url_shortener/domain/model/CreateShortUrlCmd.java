package com.hyeon.url_shortener.domain.model;

public record CreateShortUrlCmd(
        String originalUrl
) {
}
