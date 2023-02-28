package com.example.kistrading.domain.StockCode.repository;

import com.example.kistrading.domain.StockCode.entity.StockCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCodeRepository extends JpaRepository<StockCode, Integer> {
    void deleteByCode(String code);

    Optional<StockCode> findByCode(String code);
}
