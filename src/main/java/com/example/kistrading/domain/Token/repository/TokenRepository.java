package com.example.kistrading.domain.Token.repository;

import com.example.kistrading.domain.Token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
