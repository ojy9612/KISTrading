package com.example.kistrading.domain.stockcode.repository;

import com.example.kistrading.domain.stockcode.entity.StockCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCodeRepository extends JpaRepository<StockCode, Integer> {
    Optional<StockCode> findByCode(String code);
}
