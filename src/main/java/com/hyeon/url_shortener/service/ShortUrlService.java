package com.hyeon.url_shortener.service;

import com.hyeon.url_shortener.ApplicationProperties;
import com.hyeon.url_shortener.domain.entity.ShortUrl;
import com.hyeon.url_shortener.domain.entity.User;
import com.hyeon.url_shortener.domain.model.*;
import com.hyeon.url_shortener.repository.ShortUrlRepository;
import com.hyeon.url_shortener.repository.UserRedisRepository;
import com.hyeon.url_shortener.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final UserRedisRepository userRedisRepository;
    private final ApplicationProperties properties;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // short url을 만들기 위한 변수들
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            UserRepository userRepository,
            UserRedisRepository userRedisRepository,
            ApplicationProperties properties
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.userRedisRepository = userRedisRepository;
        this.properties = properties;
    }

    // private가 아닌 short url들을 모두 조회
    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDataPage = shortUrlRepository
                .findPublicShortUrls(pageable)
                .map(ShortUrlDto::fromEntity);

        return PagedResult.from(shortUrlDataPage);
    }

    // userId를 통한 short urls 조회
    public PagedResult<ShortUrlDto> getUserShortUrls(Long userId, int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlPage = shortUrlRepository.findByCreatedById(userId, pageable)
                .map(ShortUrlDto::fromEntity);
        return PagedResult.from(shortUrlPage);
    }

    // admin에서 사용할 전체 short urls 조회
    public PagedResult<ShortUrlDto> findAllShortUrls(int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlsPage = shortUrlRepository.findAllShortUrls(pageable)
                .map(ShortUrlDto::fromEntity);
        return PagedResult.from(shortUrlsPage);
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
            User createdBy;
            Optional<UserRedisDto> foundUser = userRedisRepository.findById(cmd.userId());
            
            // redis 로직
            if (foundUser.isEmpty()) {
                logger.info("Redis에 데이터가 없습니다.");
                createdBy = userRepository.findById(cmd.userId()).orElseThrow();
                UserRedisDto userRedisDto = UserRedisDto.fromEntity(createdBy);
                userRedisRepository.save(userRedisDto);
            } else {
                logger.info("Redis에 데이터가 있습니다.");
                createdBy = User.fromRedisDto(foundUser.get());
            }
            
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

    // user id에 따른 short url 삭제
    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepository.deleteByIdInAndCreatedById(ids, userId);
        }
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

    // 중복 pagenation 부분 분리
    public Pageable getPageable(int pageNo, int pageSize) {
        pageNo = pageNo > 1 ? pageNo - 1 : 0;
        return PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "createdBy");
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
}
