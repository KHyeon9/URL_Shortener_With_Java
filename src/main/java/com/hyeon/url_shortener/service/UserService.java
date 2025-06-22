package com.hyeon.url_shortener.service;

import com.hyeon.url_shortener.domain.entity.User;
import com.hyeon.url_shortener.domain.model.CreateUserCmd;
import com.hyeon.url_shortener.domain.model.UserDto;
import com.hyeon.url_shortener.domain.model.UserRedisDto;
import com.hyeon.url_shortener.repository.UserRedisRepository;
import com.hyeon.url_shortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRedisRepository userRedisRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRedisRepository userRedisRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRedisRepository = userRedisRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(CreateUserCmd cmd) {
        if (userRepository.existsByEmail(cmd.email())) {
            throw new RuntimeException("사용중인 Email입니다.");
        }

        User user = User.of(
                cmd.email(),
                passwordEncoder.encode(cmd.password()),
                cmd.name(),
                cmd.role(),
                Instant.now()
        );
        User saveUser = userRepository.save(user);


        // Redis 로직
        UserRedisDto userDto = UserRedisDto.fromEntity(saveUser);
        System.out.println("UserDto : " + userDto.id());
        userRedisRepository.save(userDto);

    }
}
