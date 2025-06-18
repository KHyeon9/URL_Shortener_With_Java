package com.hyeon.url_shortener.repository;

import com.hyeon.url_shortener.domain.entity.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    // open-in-view를 false로 하고 한번에 조회 되도록 
    // jpql fetch join이나 entity graph 등을 사용해 한번에 조회해야 n + 1 문제 해결 가능
    // isPrivate의 값이 False인 경우 리스트 반환
    //
    // @EntityGraph(attributePaths = {"createdBy"})
    //    @Query(
    //            "select su " +
    //            "from ShortUrl su " +
    //            "where su.isPrivate = false " +
    //            "order by su.createdAt desc"
    //    )
    @Query(
            "select su " +
            "from ShortUrl su " +
            "left join fetch su.createdBy " +
            "where su.isPrivate = false"
    )
    Page<ShortUrl> findPublicShortUrls(Pageable pageable);
    
    // short key가 존재하는지 확인
    boolean existsByShortKey(String shortKey);

    // short key로 short url 찾기
    Optional<ShortUrl> findByShortKey(String shortKey);

    // user id로 short urls 찾기
    @Query(
            "SELECT s " +
            "FROM ShortUrl s " +
            "LEFT JOIN FETCH s.createdBy " +
            "WHERE s.createdBy.id = :userId"
    )
    Page<ShortUrl> findByCreatedById(@Param("userId") Long userId, Pageable pageable);

    // 어드민에서 사용할 모든 short urls 찾기
    @Query(
            "select u " +
            "from ShortUrl u " +
            "left join fetch u.createdBy"
    )
    Page<ShortUrl> findAllShortUrls(Pageable pageable);

    // shrot urls 삭제
    @Modifying
    void deleteByIdInAndCreatedById(List<Long> ids, Long userId);
}
