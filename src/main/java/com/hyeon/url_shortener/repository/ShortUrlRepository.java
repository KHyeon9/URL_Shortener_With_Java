package com.hyeon.url_shortener.repository;

import com.hyeon.url_shortener.domain.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    // isPrivate의 값이 False인 경우 리스트 반환
    @Query(
            "select su " +
            "from ShortUrl su " +
            "where su.isPrivate = false " +
            "order by su.createdAt desc"
    )
    List<ShortUrl> findPublicShortUrls();
}
