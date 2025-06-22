package com.hyeon.url_shortener.repository;

import com.hyeon.url_shortener.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 user 찾기
    Optional<User> findByEmail(String email);
    // 이메일이 있는지 확인
    boolean existsByEmail(String email);
}