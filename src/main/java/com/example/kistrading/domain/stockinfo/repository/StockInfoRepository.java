package com.example.kistrading.domain.stockinfo.repository;

import com.example.kistrading.domain.stockinfo.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    List<StockInfo> findByCodeIn(List<String> codeList);
}
