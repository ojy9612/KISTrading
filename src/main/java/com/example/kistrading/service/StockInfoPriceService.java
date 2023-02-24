package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.domain.StockCode.entity.StockCode;
import com.example.kistrading.domain.StockCode.repository.StockCodeRepository;
import com.example.kistrading.domain.StockInfo.entity.StockInfo;
import com.example.kistrading.domain.StockInfo.repository.StockInfoRepository;
import com.example.kistrading.domain.StockPrice.entity.StockPrice;
import com.example.kistrading.domain.StockPrice.repository.StockPriceRepository;
import com.example.kistrading.dto.StockInfoPriceResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoPriceService {

    private final WebClientKISConnector<StockInfoPriceResDto> webClientKISConnectorStockInfoPriceResDto;
    private final StockInfoRepository stockInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockCodeRepository stockCodeRepository;
    private final TokenService tokenService;
    private final HolidayService holidayService;

    private final PropertiesMapping pm;

    /**
     * 종목코드를 통해 일봉 데이터를 DB에 추가한다.
     * 일봉 갯수의 default 는 8000개 이며 DB에 등록된 가장 최근 데이터를 기준으로 값을 넣는다.
     *
     * @param stockCodeList 종목코드 리스트
     * @deprecated (KIS에 요청하는 것이 아닌 크롤링을 통해 주가데이터를 받아올 예정)
     */
    @Transactional
    @Deprecated(since = "2023/02/24")
    public synchronized void createManyStockInfoPrices(List<String> stockCodeList) {
        final int DELTA = 4000;
        LocalDateTime now = holidayService.checkAvailableDate();

        for (String code : stockCodeList) {
            Optional<StockInfo> opCode = stockInfoRepository.findByCode(code);
            LocalDateTime date;
            long delta;

            if (opCode.isPresent()) {
                StockInfo stockInfo = opCode.get();
                List<StockPrice> stockPriceList = stockInfo.getStockPriceList();
                if (!stockPriceList.isEmpty()) {
                    date = stockPriceList.get(0).getDate().atStartOfDay();

                    delta = Duration.between(date, now).toDays();

                } else {
                    delta = DELTA; // 약 22년 치
                }
            } else {
                delta = DELTA;
            }

            LocalDateTime tempNow = now.plusDays(1);
            while (delta > 0) {
                LocalDateTime start;
                LocalDateTime end;

                if (delta > 140) {
                    start = tempNow.minusDays(140);
                    end = tempNow.minusDays(1);
                    tempNow = start;
                    delta -= 140;
                } else {
                    start = tempNow.minusDays(delta);
                    end = tempNow.minusDays(1);
                    tempNow = start;
                    delta = 0;
                }

                boolean isNext = createStockInfoPrice(code, start, end);

                if (!isNext) {
                    break;
                }
            }
        }
    }

    /**
     * 종목 하나에 대해 StockInfo 를 업데이트, 생성 하며 일봉데이터를 넣는다.(최대 100개, 약 97개 불러옴)
     *
     * @param stockCode 종목코드
     * @param start     일봉 데이터 시작일
     * @param end       일봉 데이터 마지막일
     * @return boolean
     * @deprecated (KIS에 요청하는 것이 아닌 크롤링을 통해 주가데이터를 받아올 예정)
     */
    @Transactional
    @Deprecated(since = "2023/02/24")
    public boolean createStockInfoPrice(String stockCode, LocalDateTime start, LocalDateTime end) {

        Map<String, String> reqHeaders = new HashMap<>();
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();

        reqHeaders.put("authorization", tokenService.checkGetToken());
        reqHeaders.put("appkey", pm.getAppKey());
        reqHeaders.put("appsecret", pm.getAppSecret());
        reqHeaders.put("tr_id", "FHKST03010100");

        reqParams.set("FID_COND_MRKT_DIV_CODE", "J");
        reqParams.set("FID_INPUT_ISCD", stockCode);
        reqParams.set("FID_INPUT_DATE_1", start.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        reqParams.set("FID_INPUT_DATE_2", end.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        reqParams.set("FID_PERIOD_DIV_CODE", "D");
        reqParams.set("FID_ORG_ADJ_PRC", "0");

        StockInfoPriceResDto response = webClientKISConnectorStockInfoPriceResDto.connect(HttpMethod.GET, "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice",
                reqHeaders, reqParams, null, StockInfoPriceResDto.class);

        if (response.getRtCd().equals("0")) {
            Optional<StockInfo> isExist = stockInfoRepository.findByCode(stockCode);
            StockInfo stockInfo;
            StockInfoPriceResDto.Output1 output1 = response.getOutput1();
            if (isExist.isPresent()) {
                stockInfo = isExist.get();
                stockInfo.updateBuilder()
                        .name(output1.getHtsKorIsnm())
                        .code(stockCode)
                        .otherCode(output1.getStckShrnIscd())
                        .fcam(output1.getStckFcam())
                        .amount(Long.valueOf(output1.getLstnStcn()))
                        .marketCapitalization(output1.getHtsAvls())
                        .capital(output1.getCpfn())
                        .per(output1.getPer())
                        .pbr(output1.getPbr())
                        .eps(output1.getEps())
                        .build();
            } else {
                stockInfo = StockInfo.builder()
                        .name(output1.getHtsKorIsnm())
                        .code(stockCode)
                        .otherCode(output1.getStckShrnIscd())
                        .fcam(output1.getStckFcam())
                        .amount(Long.valueOf(output1.getLstnStcn()))
                        .marketCapitalization(output1.getHtsAvls())
                        .capital(output1.getCpfn())
                        .per(output1.getPer())
                        .pbr(output1.getPbr())
                        .eps(output1.getEps())
                        .build();
            }
            stockInfoRepository.save(stockInfo);

            List<StockPrice> stockPriceList = new ArrayList<>();

            int checkNullCnt = 0;
            for (StockInfoPriceResDto.Output2 output2 : response.getOutput2()) {
                if (output2.getStckBsopDate() == null) {
                    checkNullCnt++;
                    continue;
                }

                stockPriceList.add(StockPrice.builder()
                        .date(LocalDate.parse(output2.getStckBsopDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .closePrice(new BigDecimal(output2.getStckClpr()))
                        .openPrice(new BigDecimal(output2.getStckOprc()))
                        .highPrice(new BigDecimal(output2.getStckHgpr()))
                        .lowPrice(new BigDecimal(output2.getStckLwpr()))
                        .volume(Long.valueOf(output2.getAcmlVol()))
                        .volumeTotalPrice(new BigDecimal(output2.getAcmlTrPbmn()))
                        .beforeGapPrice(new BigDecimal(output2.getPrdyVrss()))
                        .beforeGapPriceSign(output2.getPrdyVrssSign())
                        .stockInfo(stockInfo)
                        .build());

            }

            stockPriceRepository.saveAll(stockPriceList);

            return checkNullCnt < response.getOutput2().size();
        }
        throw new RuntimeException("KIS 통신 에러");
    }

    /**
     * 신규 상장된 종목의 StockInfo, 일봉데이터를 넣는다.
     * createStockInfoPrice() 함수를 통해 데이터를 넣을 수도 있지만 신규 상장된 목록을 따로 보기위해 만들었다.
     *
     * @deprecated (KIS에 요청하는 것이 아닌 크롤링을 통해 주가데이터를 받아올 예정)
     */
    @Transactional
    @Deprecated(since = "2023/02/24")
    public void checkNewStock() {
        Set<String> codeSet = stockCodeRepository.findAll().stream().map(StockCode::getCode).collect(Collectors.toSet());
        Set<String> infoSet = stockInfoRepository.findAll().stream().map(StockInfo::getCode).collect(Collectors.toSet());

        codeSet.removeAll(infoSet);

        if (codeSet.isEmpty()) {
            log.info("신규 상장된 종목이 없습니다.");
        } else {
            log.info("신규 상장된 주식 수" + codeSet.size());
            this.createManyStockInfoPrices(codeSet.stream().toList());
        }
        for (String s : codeSet) {
            log.info("신규 상장된 주식 코드: " + s);
        }
    }


    /**
     * DB에 저장된 모든 종목코드를 불러온다.
     *
     * @return List<String> 종목코드 List
     */
    @Transactional(readOnly = true)
    public List<String> getStockCodeList() {
        return stockInfoRepository.findAll().stream().map(StockInfo::getCode).toList();
    }
}
