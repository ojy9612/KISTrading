package com.example.kistrading.repository;

import com.example.kistrading.entity.StockCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockCodeRepository extends JpaRepository<StockCode, Integer> {
    void deleteByCode(String code);
}
