package com.example.kistrading.controller;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.domain.common.em.OrderType;
import com.example.kistrading.domain.common.service.AssetService;
import com.example.kistrading.domain.common.service.NaverFinanceCrawlerService;
import com.example.kistrading.domain.common.service.TradeService;
import com.example.kistrading.domain.common.service.WebClientKISConnector;
import com.example.kistrading.domain.holiday.service.HolidayService;
import com.example.kistrading.domain.stockcode.entity.StockCode;
import com.example.kistrading.domain.stockcode.service.StockCodeService;
import com.example.kistrading.domain.stockinfo.repository.StockInfoRepository;
import com.example.kistrading.domain.stockinfo.service.StockInfoService;
import com.example.kistrading.domain.stockprice.service.StockPriceService;
import com.example.kistrading.domain.token.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    private final StockPriceService stockPriceService;

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
        List<String> stockCodeList = stockCodeService.getStockCodeList().stream().map(StockCode::getCode).toList();
        int partitionSize = stockCodeList.size() / 10;
        List<List<String>> partitionedList = IntStream.range(0, 10)
                .mapToObj(i -> stockCodeList.subList(i * partitionSize, i == 9 ? stockCodeList.size() : (i + 1) * partitionSize)).toList();


        for (int i = 0; i < partitionedList.size(); i++) {
            log.info((i + 1) + "번째 시작 / " + 10);
            stockInfoService.upsertStockInfo(partitionedList.get(i), holidayService.deltaOneAvailableDate(), holidayService.deltaTwoAvailableDate());
            stockPriceService.upsertStockPrice(partitionedList.get(i), holidayService.getAvailableDate());
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

    }

}
