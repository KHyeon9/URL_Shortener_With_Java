package com.hyeon.url_shortener.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlForm(
        @NotBlank(message = "Original URL을 필수로 입력해야 합니다.")
        String originalUrl,
        Boolean isPrivate,
        @Min(1)
        @Max(365)
        Integer expirationInDays
) {
}
