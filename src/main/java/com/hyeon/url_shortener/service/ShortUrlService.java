package com.hyeon.url_shortener.service;

import com.hyeon.url_shortener.ApplicationProperties;
import com.hyeon.url_shortener.domain.entity.ShortUrl;
import com.hyeon.url_shortener.domain.entity.User;
import com.hyeon.url_shortener.domain.model.CreateShortUrlCmd;
import com.hyeon.url_shortener.domain.model.ShortUrlDto;
import com.hyeon.url_shortener.repository.ShortUrlRepository;
import com.hyeon.url_shortener.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final ApplicationProperties properties;
    // short url을 만들기 위한 변수들
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository, ApplicationProperties properties) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.properties = properties;
    }

    // private가 아닌 short url들을 모두 조회
    public List<ShortUrlDto> findAllPublicShortUrls() {

        return shortUrlRepository.findPublicShortUrls()
                .stream().map(ShortUrlDto::fromEntity).toList();
    }

    // short url 생성
    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if (properties.validateOriginalUrl()) {
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            // url이 유효한지 확인
            if (!urlExists) {
                throw new RuntimeException("URL이 유효하지 않습니다: " + cmd.originalUrl());
            }
        }

        String shortKey = generateUniqueShortKey();
        ShortUrl shortUrl;

        if (cmd.userId() == null) {
            shortUrl = ShortUrl.of(
                    shortKey,
                    cmd.originalUrl(),
                    Instant.now().plus(properties.defaultExpiryInDays(), ChronoUnit.DAYS),
                    0L,
                    Instant.now()
            );
        } else {
            User createdBy = userRepository.findById(cmd.userId()).orElseThrow();
            Boolean isPrivate = cmd.isPrivate() == null ? false : cmd.isPrivate();
            Instant expiredAt = cmd.expirationInDays() != null ?
                    Instant.now().plus(cmd.expirationInDays(), ChronoUnit.DAYS) : null;

            shortUrl = ShortUrl.of(
                    shortKey,
                    cmd.originalUrl(),
                    isPrivate,
                    expiredAt,
                    createdBy,
                    0L,
                    Instant.now()
            );
        }

        shortUrlRepository.save(shortUrl);

        return ShortUrlDto.fromEntity(shortUrl);
    }

    // 유니크한 short key 생성
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

    // shortKey를 가지고 원래 주소 가져오기
    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {
        Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortKey(shortKey);

        if (shortUrlOptional.isEmpty()) {
            return Optional.empty();
        }

        ShortUrl shortUrl = shortUrlOptional.get();
        System.out.println("shortUrl : " + shortUrl.getOriginalUrl());

        if (shortUrl.getExpiredAt() != null && shortUrl.getExpiredAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        
        // private인 주소를 다른 유저가 접근 못하도록 함
        if (shortUrl.getIsPrivate() != null && shortUrl.getCreatedBy() != null &&
                !Objects.equals(shortUrl.getCreatedBy().getId(), userId)) {
            return Optional.empty();
        }

        // 클릭 카운트 증가
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);
        return Optional.of(ShortUrlDto.fromEntity(shortUrl));
    }
}
