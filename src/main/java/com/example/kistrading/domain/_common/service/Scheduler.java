package com.example.kistrading.domain._common.service;

import com.example.kistrading.domain._common.dto.AccountDataResDto;
import com.example.kistrading.domain.holiday.service.HolidayService;
import com.example.kistrading.domain.stockcode.service.StockCodeService;
import com.example.kistrading.domain.stockinfo.service.StockInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final StockCodeService stockCodeService;
    private final StockInfoService stockInfoService;
    private final AssetService assetService;
    private final TradeService tradeService;
    private final HolidayService holidayService;

    @Scheduled(cron = "0 0 16 * * *")
    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = {"deltaOneDay", "deltaTwoDay", "availableDate"})
    public void evictCache() {
    }

    /* 초 분 시 일 월 요일, [참고문서:https://zamezzz.tistory.com/197] */
//    @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시에 실행
    public void tradeSetting() {

        holidayService.createHolidaysByYear(LocalDate.now().getYear());


        if (holidayService.isHoliday(LocalDate.now())) {
            log.info("오늘은 공휴일 입니다.");

        } else {
            log.info("오늘은 개장일 입니다.");
            stockCodeService.upsertStockCode(); // 신규,수정 된 종목코드 불러오기
            stockInfoService.checkNewStock(); // 신규 종목 업데이트
        }


        List<AccountDataResDto> accountDataList = assetService.getAccountData(); // 계좌 정보

    }
}
