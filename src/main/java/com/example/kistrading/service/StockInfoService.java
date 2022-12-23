package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockInfoService {

    private final WebClientConnector<String> webClientConnector;

    private final PropertiesMapping pm;

    public void getAllStockInfo(){

    }

    public void getStockInfo(){

    }
}
