package com.hyeon.url_shortener.repository;

import com.hyeon.url_shortener.domain.model.UserRedisDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedisDto, Long> {
}
