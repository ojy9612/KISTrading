package com.example.kistrading.domain.StockPrice.repository;

import com.example.kistrading.domain.StockPrice.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
