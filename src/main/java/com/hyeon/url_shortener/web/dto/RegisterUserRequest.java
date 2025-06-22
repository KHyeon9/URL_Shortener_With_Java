package com.hyeon.url_shortener.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 양식에 맞춰주세요.")
        String email,
        @NotBlank(message = "패스워드는 필수입니다.")
        String password,
        @NotBlank(message = "이름은 필수입니다.")
        String name
) {
}
