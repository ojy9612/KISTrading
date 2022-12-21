package com.example.kistrading.repository;

import com.example.kistrading.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
