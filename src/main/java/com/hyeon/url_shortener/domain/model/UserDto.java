package com.hyeon.url_shortener.domain.model;

import com.hyeon.url_shortener.domain.entity.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(
        Long id,
        String name
) implements Serializable {

  public static UserDto fromEntity(User entity) {
    return new UserDto(entity.getId(), entity.getName());
  }
}