package com.hyeon.url_shortener.repository;

import com.hyeon.url_shortener.domain.entity.ShortUrl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    // open-in-view를 false로 하고 한번에 조회 되도록 
    // jpql fetch join이나 entity graph 등을 사용해 한번에 조회해야 n + 1 문제 해결 가능
    // isPrivate의 값이 False인 경우 리스트 반환
    //    @Query(
    //            "select su " +
    //            "from ShortUrl su " +
    //            "left join fetch su.createdBy " +
    //            "where su.isPrivate = false " +
    //            "order by su.createdAt desc"
    //    )
    @Query(
            "select su " +
            "from ShortUrl su " +
            "where su.isPrivate = false " +
            "order by su.createdAt desc"
    )
    @EntityGraph(attributePaths = {"createdBy"})
    List<ShortUrl> findPublicShortUrls();
}
