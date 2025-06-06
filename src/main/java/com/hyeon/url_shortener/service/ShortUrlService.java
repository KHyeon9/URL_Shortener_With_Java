package com.hyeon.url_shortener.service;

import com.hyeon.url_shortener.domain.model.ShortUrlDto;
import com.hyeon.url_shortener.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    // private가 아닌 short url들을 모두 조회
    public List<ShortUrlDto> findAllPublicShortUrls() {

        return shortUrlRepository.findPublicShortUrls()
                .stream().map(ShortUrlDto::fromEntity).toList();
    }
}
