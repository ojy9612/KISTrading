package com.example.kistrading.domain.stockprice.repository;

import com.example.kistrading.domain.stockprice.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
