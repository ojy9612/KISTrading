package com.example.kistrading.domain.stockprice.service;

import com.example.kistrading.domain.common.service.NaverFinanceCrawlerService;
import com.example.kistrading.domain.stockinfo.entity.StockInfo;
import com.example.kistrading.domain.stockinfo.service.StockInfoService;
import com.example.kistrading.domain.stockprice.entity.StockPrice;
import com.example.kistrading.domain.stockprice.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPriceService {
    private final StockPriceRepository stockPriceRepository;
    private final StockInfoService stockInfoService;
    private final NaverFinanceCrawlerService naverFinanceCrawlerService;


    /**
     * {@link NaverFinanceCrawlerService} 를 이용해 naverFinance에서 주가데이터를 받아와 저장, 업데이트 한다.
     *
     * @param stockCodeList stockCodeList
     * @param stdDay        기준 일자
     */
    public void upsertStockPrice(List<String> stockCodeList, LocalDate stdDay) {
        /* n+1 문제를 해결하기 위해 전체 데이터를 받아온 뒤 Map에 넣는다. */
        List<StockInfo> stockInfoList = stockInfoService.getStockInfoList(stockCodeList);
        Map<String, StockInfo> stockInfoMap = new HashMap<>();
        Map<String, StockPrice> stockPriceMap = new HashMap<>();
        for (StockInfo stockInfo : stockInfoList) {
            stockInfoMap.put(stockInfo.getCode(), stockInfo);
            List<StockPrice> stockPriceList = stockInfo.getStockPriceList();
            for (StockPrice stockPrice : stockPriceList) {
                stockPriceMap.put(stockInfo.getCode() + stockPrice.getDate(), stockPrice);
            }
        }
        /* ----- */

        List<StockPrice> stockPriceListForSave = new ArrayList<>();
        for (String stockCode : stockCodeList) {
            log.info(stockCode + " 시작 : " + LocalDateTime.now());
            StockInfo stockInfo = stockInfoMap.getOrDefault(stockCode, null);

            // stockCode에 해당하는 StockInfo가 DB에 없으므로 무시
            if (stockInfo == null) {
                continue;
            }

            String result = naverFinanceCrawlerService.crawlStockPrice(stockCode, stdDay);

            /* 받아온 데이터를 형식에 맞게 추출 후 db에 저장 */
            Pattern p = Pattern.compile("\\[([^\\[\\]]*)]");
            Matcher m = p.matcher(result);

            // 첫 번째 라인 스킵
            if (m.find()) {
                m.group(1);
            }

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

                LocalDate date = (LocalDate) row.get(0);
                StockPrice stockPrice = stockPriceMap.getOrDefault(stockCode + date, null);

                if (stockPrice == null) {
                    stockPriceListForSave.add(StockPrice.builder()
                            .date(date)
                            .closePrice((BigDecimal) row.get(4))
                            .openPrice((BigDecimal) row.get(1))
                            .highPrice((BigDecimal) row.get(2))
                            .lowPrice((BigDecimal) row.get(3))
                            .volume((Long) row.get(5))
                            .stockInfo(stockInfo)
                            .build());
                } else {
                    stockPrice.updateStockPriceBuilder()
                            .date(date)
                            .closePrice((BigDecimal) row.get(4))
                            .openPrice((BigDecimal) row.get(1))
                            .highPrice((BigDecimal) row.get(2))
                            .lowPrice((BigDecimal) row.get(3))
                            .volume((Long) row.get(5))
                            .stockInfo(stockInfo)
                            .build();
                }

            }
            /* ----- */
        }

        stockPriceRepository.saveAll(stockPriceListForSave);
    }
}
