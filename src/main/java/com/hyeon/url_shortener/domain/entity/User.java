package com.hyeon.url_shortener.domain.entity;

import com.hyeon.url_shortener.domain.model.Role;
import com.hyeon.url_shortener.domain.model.UserRedisDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ColumnDefault("'ROLE_USER'")
    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected User() {}

    private User(Long id, String email, String password, String name, Role role, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static User fromRedisDto(UserRedisDto dto) {
        return User.of(
                dto.id(),
                dto.email(),
                dto.password(),
                dto.name(),
                dto.role(),
                dto.createdAt()
        );
    }

    public static User of (Long id, String email, String password, String name, Role role, Instant createdAt) {
        return new User(id, email, password, name, role, createdAt);
    }

    public static User of (String email, String password, String name, Role role, Instant createdAt) {
        return new User(null, email, password, name, role, createdAt);
    }
}