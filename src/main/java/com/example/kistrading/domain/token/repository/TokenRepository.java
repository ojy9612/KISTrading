package com.example.kistrading.domain.token.repository;

import com.example.kistrading.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
