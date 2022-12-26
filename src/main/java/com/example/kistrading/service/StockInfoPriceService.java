package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.StockCodeResDto;
import com.example.kistrading.dto.StockInfoPriceResDto;
import com.example.kistrading.entity.StockCode;
import com.example.kistrading.entity.StockInfo;
import com.example.kistrading.entity.StockPrice;
import com.example.kistrading.repository.StockCodeRepository;
import com.example.kistrading.repository.StockInfoRepository;
import com.example.kistrading.repository.StockPriceRepository;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoPriceService {

    private final WebClientKISConnector<StockInfoPriceResDto> webClientKISConnectorStockInfoPriceResDto;
    private final WebClientDataGoKrConnector<StockCodeResDto> webClientDataGoKrConnectorStockCodeResDto;
    private final TokenService tokenService;
    private final StockInfoRepository stockInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockCodeRepository stockCodeRepository;

    private final PropertiesMapping pm;

    @Transactional
    public synchronized void createManyStockInfoPrices(List<String> stockCodeList) { // 시작 날짜 기준으로 최신 값으로 채우기;
        LocalDateTime now;
        if (LocalTime.now().compareTo(LocalTime.of(16, 0)) < 0) {
            now = LocalDateTime.now().minusDays(1);
        } else {
            now = LocalDateTime.now();
        }

        for (String code : stockCodeList) {
            Optional<StockInfo> opCode = stockInfoRepository.findByCode(code);
            LocalDateTime date;
            long delta;

            if (opCode.isPresent()) {
                StockInfo stockInfo = opCode.get();
                List<StockPrice> stockPriceList = stockInfo.getStockPriceList();
                if (!stockPriceList.isEmpty()) {
                    date = stockPriceList.get(0).getDate();

                    delta = Duration.between(date, now).toDays();

                } else {
                    delta = 8000; // 약 22년 치
                }
            } else {
                delta = 8000;
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

    @Transactional
    public boolean createStockInfoPrice(String stockCode, LocalDateTime start, LocalDateTime end) {

        Map<String, String> reqHeaders = new HashMap<>();
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();

        reqHeaders.put("authorization", pm.checkGetToken());
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
                        .date(LocalDate.parse(output2.getStckBsopDate(), DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay())
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

    @Transactional
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

    @Transactional
    public void updateStockCode() {
        int pageNo = 1;
        int totalCount = Integer.MAX_VALUE;
        List<StockCodeResDto.Item> bodyList = new ArrayList<>();
        StockCodeResDto response;
        String beforeDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        reqParam.set("resultType", "json");
        reqParam.set("numOfRows", "1000");
        reqParam.set("basDt", beforeDate);

        while (totalCount > 1000 * (pageNo - 1)) {
            reqParam.set("pageNo", String.valueOf(pageNo));
            response = webClientDataGoKrConnectorStockCodeResDto.connect(HttpMethod.GET,
                    "1160100/service/GetKrxListedInfoService/getItemInfo",
                    null, reqParam, null, StockCodeResDto.class);

            totalCount = response.getResponse().getBody().getTotalcount();
            pageNo++;
            bodyList.addAll(response.getResponse().getBody().getItems().getItem());
        }

        List<StockCode> stockCodeList = bodyList.stream().filter(item -> item.getMrktctg().equals("KOSDAQ") || item.getMrktctg().equals("KOSPI"))
                .map(body -> StockCode.builder()
                        .name(body.getItmsnm())
                        .code(body.getSrtncd().substring(1))
                        .market(body.getMrktctg())
                        .build()
                ).toList();

        List<StockCode> all = stockCodeRepository.findAll();

        List<StockCode> newStockCodeList = all.stream().filter(stockCode -> {
            for (StockCode code : stockCodeList) {
                if (code.getCode().equals(stockCode.getCode())) {
                    if (!code.getName().equals(stockCode.getName()) || !code.getMarket().equals(stockCode.getName())) {
                        stockCodeRepository.deleteByCode(code.getCode());
                    }
                    return false;
                }
            }
            return true;
        }).toList();

        stockCodeRepository.saveAll(newStockCodeList);
    }

    @Transactional(readOnly = true)
    public List<String> getStockCodeList() {
        return stockInfoRepository.findAll().stream().map(StockInfo::getCode).toList();
    }
}
