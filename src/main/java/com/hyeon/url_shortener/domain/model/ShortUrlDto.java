package com.hyeon.url_shortener.domain.model;

import com.hyeon.url_shortener.domain.entity.ShortUrl;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ShortUrl}
 */
public record ShortUrlDto(
        Long id,
        String shortKey,
        String originalUrl,
        Boolean isPrivate,
        Instant expiredAt,
        UserDto createdBy,
        Long clickCount,
        Instant createdAt
) implements Serializable {

    public static ShortUrlDto fromEntity(ShortUrl entity) {
        UserDto userDto = null;
        if (entity.getCreatedBy() != null) {
            userDto = UserDto.fromEntity(entity.getCreatedBy());
        }
        return new ShortUrlDto(
                entity.getId(),
                entity.getShortKey(),
                entity.getOriginalUrl(),
                entity.getIsPrivate(),
                entity.getExpiredAt(),
                userDto,
                entity.getClickCount(),
                entity.getCreatedAt()
        );
    }
}