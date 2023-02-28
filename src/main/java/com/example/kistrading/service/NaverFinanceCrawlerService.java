package com.example.kistrading.service;

import com.example.kistrading.domain.StockInfo.entity.StockInfo;
import com.example.kistrading.domain.StockInfo.repository.StockInfoRepository;
import com.example.kistrading.domain.StockPrice.entity.StockPrice;
import com.example.kistrading.domain.StockPrice.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//         System.setProperty("webdriver.chrome.driver", "/Users/ohjaeyeong/zeki_folder/resource/chromedriver/chromedriver");
@Service
@RequiredArgsConstructor
public class NaverFinanceCrawlerService {

    private final WebClientCommonConnector<String> stringWebClientCommonConnector;
    private final StockPriceRepository stockPriceRepository;
    private final StockInfoRepository stockInfoRepository;

    @Transactional
    public void crawlStockPrice(String stockCode, LocalDate stdDay) {
        String symbol = stockCode;
        String requestType = "2";
        int count = 5000;
        String startTime = stdDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeframe = "day";

        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("symbol", symbol);
        reqParam.add("requestType", requestType);
        reqParam.add("count", String.valueOf(count));
        reqParam.add("startTime", startTime);
        reqParam.add("timeframe", timeframe);

        String result = stringWebClientCommonConnector.connect(HttpMethod.GET, "api.finance.naver.com/siseJson.naver", null, reqParam, null, String.class);

        Pattern p = Pattern.compile("\\[([^\\[\\]]*)]");
        Matcher m = p.matcher(result);

        // 첫 번째 라인 스킵
        if (m.find()) {
            m.group(1);
        }
        StockPrice stockPrice = null;
        while (m.find()) {
            String[] items = m.group(1).split(",");
            List<Object> row = new ArrayList<>();

            for (int i = 0; i < items.length - 1; i++) {
                String item = items[i].replaceAll("[^0-9.+-]", "");

                if (i == 0) {
                    row.add(LocalDate.parse(item, DateTimeFormatter.ofPattern("yyyyMMdd")));
                } else if (items.length - 2 == i) {
                    row.add(Long.parseLong(item));
                } else {
                    row.add(new BigDecimal(item));
                }
            }

            try {
                StockInfo stockInfo = stockInfoRepository.findByCode(stockCode).orElseThrow(
                        IllegalArgumentException::new);

                stockPrice = StockPrice.builder()
                        .date((LocalDate) row.get(0))
                        .closePrice((BigDecimal) row.get(4))
                        .openPrice((BigDecimal) row.get(1))
                        .highPrice((BigDecimal) row.get(2))
                        .lowPrice((BigDecimal) row.get(3))
                        .volume((Long) row.get(5))
                        .stockInfo(stockInfo)
                        .build();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("id가 없습니다. " + stockCode);
            }
        }

        // TODO: 값이 null인 경우 해결

        stockPriceRepository.save(stockPrice);
    }


}
