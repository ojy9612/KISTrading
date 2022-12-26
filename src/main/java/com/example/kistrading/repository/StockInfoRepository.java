package com.example.kistrading.repository;

import com.example.kistrading.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    Optional<StockInfo> findByCode(String code);

    List<StockInfo> findByCodeIn(List<String> codes);
}
