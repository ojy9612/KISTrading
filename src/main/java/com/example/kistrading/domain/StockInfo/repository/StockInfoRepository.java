package com.example.kistrading.domain.StockInfo.repository;

import com.example.kistrading.domain.StockInfo.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    Optional<StockInfo> findByCode(String code);
}
