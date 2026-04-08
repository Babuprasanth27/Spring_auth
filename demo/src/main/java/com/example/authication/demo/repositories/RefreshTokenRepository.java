package com.example.authication.demo.repositories;

import com.example.authication.demo.entities.RefreshToken;
import com.example.authication.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
