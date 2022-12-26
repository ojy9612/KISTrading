package com.example.kistrading.controller;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.entity.StockInfo;
import com.example.kistrading.entity.em.OrderType;
import com.example.kistrading.repository.StockInfoRepository;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final PropertiesMapping pm;

    private final WebClientKISConnector<String> webClientKISConnectorString;
    private final TokenService tokenService;
    private final TradeService tradeService;
    private final AssetService assetService;
    private final StockInfoPriceService stockInfoPriceService;
    private final HolidayService holidayService;

    private final StockInfoRepository stockInfoRepository;

    private final ObjectMapper objectMapper;


    @GetMapping("/test1")
    public String test1() {
        return tokenService.getAndDeleteToken();
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
        stockInfoPriceService.createStockInfoPrice("007680", LocalDateTime.now(), LocalDateTime.now());
    }

    @GetMapping("/test6")
    public void test6() {
        stockInfoPriceService.updateStockCode();
    }

    @GetMapping("/test8")
    public void test8() {
        List<String> codes = stockInfoRepository.findAll().stream().map(StockInfo::getCode).toList();

        int bifurcation = 1200;

        for (int i = 0; i < bifurcation; i++) {
            log.info("현재 bifurcation: " + i + "/" + bifurcation);
            stockInfoPriceService.createManyStockInfoPrices(
                    codes.subList(i * (codes.size() / bifurcation), (i + 1) * (codes.size() / bifurcation)));
        }
        if ((codes.size() % bifurcation) != 0) {
            log.info("마지막 작업중 ... ");
            stockInfoPriceService.createManyStockInfoPrices(
                    codes.subList((codes.size() / bifurcation) * bifurcation, codes.size() - 1));
        }
        log.info("마지막 끝 ");

    }

    @GetMapping("/test9")
    public void test9() {
        holidayService.createHolidaysByYear(2022);
    }

}
