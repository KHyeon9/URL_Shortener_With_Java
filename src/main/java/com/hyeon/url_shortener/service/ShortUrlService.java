package com.hyeon.url_shortener.service;

import com.hyeon.url_shortener.ApplicationProperties;
import com.hyeon.url_shortener.domain.entity.ShortUrl;
import com.hyeon.url_shortener.domain.model.CreateShortUrlCmd;
import com.hyeon.url_shortener.domain.model.ShortUrlDto;
import com.hyeon.url_shortener.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ApplicationProperties properties;
    // short url을 만들기 위한 변수들
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public ShortUrlService(ShortUrlRepository shortUrlRepository, ApplicationProperties properties) {
        this.shortUrlRepository = shortUrlRepository;
        this.properties = properties;
    }

    // private가 아닌 short url들을 모두 조회
    public List<ShortUrlDto> findAllPublicShortUrls() {

        return shortUrlRepository.findPublicShortUrls()
                .stream().map(ShortUrlDto::fromEntity).toList();
    }

    // short url 생성
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if (properties.validateOriginalUrl()) {
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            // url이 유효한지 확인
            if (!urlExists) {
                throw new RuntimeException("URL이 유효하지 않습니다: " + cmd.originalUrl());
            }
        }

        String shortKey = generateUniqueShortKey();
        ShortUrl shortUrl = ShortUrl.of(
                shortKey,
                cmd.originalUrl(),
                false,
                Instant.now().plus(properties.defaultExpiryInDays(), ChronoUnit.DAYS),
                null,
                0L,
                Instant.now()
            );

        shortUrlRepository.save(shortUrl);

        return ShortUrlDto.fromEntity(shortUrl);
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));

        return shortKey;
    }

    // 랜덤 short key 생성
    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
