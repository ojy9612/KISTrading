package com.example.kistrading.service;

import com.example.kistrading.dto.AccountDataResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final StockInfoPriceService stockInfoPriceService;
    private final AssetService assetService;
    private final TradeService tradeService;
    private final HolidayService holidayService;
    

    /* 초 분 시 일 월 요일, [참고문서:https://zamezzz.tistory.com/197] */
//    @Scheduled(cron = "0 0 8 * * ?") // 매일 오전 8시에 실행
    public void tradeSetting() {

        holidayService.createHolidaysByYear(LocalDate.now().getYear());


        if (holidayService.isHoliday(LocalDateTime.now())) {
            log.info("오늘은 공휴일 입니다.");

        } else {
            log.info("오늘은 개장일 입니다.");
            stockInfoPriceService.createManyStockInfoPrices(stockInfoPriceService.getStockCodeList()); // 종가 업데이트
            stockInfoPriceService.checkNewStock(); // 신규 종목 업데이트
        }

        stockInfoPriceService.updateStockCode(); // 신규,수정 된 종목코드 불러오기

        List<AccountDataResDto> accountDataList = assetService.getAccountData(); // 계좌 정보

    }
}
