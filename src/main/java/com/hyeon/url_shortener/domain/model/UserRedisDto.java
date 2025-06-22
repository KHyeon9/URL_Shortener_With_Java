package com.hyeon.url_shortener.domain.model;

import com.hyeon.url_shortener.domain.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@RedisHash(value = "user", timeToLive = 300)
public record UserRedisDto (
        @Id
        Long id,
        String email,
        String password,
        String name,
        Role role,
        Instant createdAt
) implements Serializable {

    public static UserRedisDto fromEntity(User user) {
        return new UserRedisDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}