package com.hyeon.url_shortener.domain.model;

public record CreateUserCmd(
        String email,
        String password,
        String name,
        Role role
) {
}
