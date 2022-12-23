package com.example.kistrading.repository;

import com.example.kistrading.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
