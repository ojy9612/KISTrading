package com.example.kistrading.controller;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.domain.StockCode.entity.StockCode;
import com.example.kistrading.domain.StockInfo.repository.StockInfoRepository;
import com.example.kistrading.domain.em.OrderType;
import com.example.kistrading.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final PropertiesMapping pm;

    private final WebClientKISConnector<String> webClientKISConnectorString;
    private final TradeService tradeService;
    private final TokenService tokenService;
    private final AssetService assetService;
    private final StockInfoService stockInfoService;
    private final StockCodeService stockCodeService;
    private final HolidayService holidayService;
    private final NaverFinanceCrawlerService naverFinanceCrawlerService;

    private final StockInfoRepository stockInfoRepository;

    private final ObjectMapper objectMapper;


    @GetMapping("/test1")
    public String test1() {
        return tokenService.checkGetToken();
    }


    @GetMapping("/test2/{token}")
    public JsonNode test2(@PathVariable String token) {

        Map<String, String> reqBody = new HashMap<>();

        reqBody.put("token", token);
        reqBody.put("appkey", pm.getAppKey());
        reqBody.put("appsecret", pm.getAppSecret());


        JsonNode test = null;
        try {
            for (int i = 0; i < 100; i++) {
                test = objectMapper.readTree(webClientKISConnectorString.connect(HttpMethod.POST, "/oauth2/revokeP",
                        null, null, reqBody, String.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return test;


    }

    @GetMapping("/test3")
    public void test3() {
        assetService.getAccountData();
    }

    @GetMapping("/test4")
    public void test4() {
        tradeService.orderStock(OrderType.BUY, "007680", "0", "10");
    }

    @GetMapping("/test5")
    public void test5() {
        LocalDate nowDate = holidayService.getAvailableDate();
        List<String> stockCodeList = stockCodeService.getStockCodeList().stream().map(StockCode::getCode).toList();

        for (String stockCode : stockCodeList) {
            stockInfoService.upsertStockInfo(stockCode, nowDate.minusDays(1), nowDate);
        }

    }

    @GetMapping("/test6")
    public void test6() {
        stockCodeService.upsertStockCode();
    }

    @GetMapping("/test8")
    public void test8() {

    }

    @GetMapping("/test9")
    public void test9() {
        holidayService.createHolidaysByYear(2023);
    }

    @GetMapping("/test10")
    public void test10() {
        naverFinanceCrawlerService.crawlStockPrice("338220", LocalDate.now());
    }

}
