package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.StockInfoPriceResDto;
import com.example.kistrading.entity.StockInfo;
import com.example.kistrading.entity.StockPrice;
import com.example.kistrading.repository.StockInfoRepository;
import com.example.kistrading.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockInfoPriceService {

    private final WebClientConnector<StockInfoPriceResDto> webClientConnectorDto;
    private final TokenService tokenService;
    private final StockInfoRepository stockInfoRepository;
    private final StockPriceRepository stockPriceRepository;

    private final PropertiesMapping pm;

    public void getAllStockInfoPrice() {

    }

    @Transactional
    public void createStockPrice(String stockCode, LocalDateTime start, LocalDateTime end) {

        Map<String, String> reqHeaders = new HashMap<>();
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();

        reqHeaders.put("authorization", tokenService.getAndDeleteToken());
        reqHeaders.put("appkey", pm.getAppKey());
        reqHeaders.put("appsecret", pm.getAppSecret());
        reqHeaders.put("tr_id", "FHKST03010100");

        reqParams.set("FID_COND_MRKT_DIV_CODE", "J");
        reqParams.set("FID_INPUT_ISCD", stockCode);
        reqParams.set("FID_INPUT_DATE_1", start.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        reqParams.set("FID_INPUT_DATE_2", end.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        reqParams.set("FID_PERIOD_DIV_CODE", "D");
        reqParams.set("FID_ORG_ADJ_PRC", "0");

        StockInfoPriceResDto response = webClientConnectorDto.connect(HttpMethod.GET, "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice",
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

            List<StockPrice> stockPriceList = response.getOutput2().stream().map(output2 -> StockPrice.builder()
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
                    .build()
            ).toList();

            stockPriceRepository.saveAll(stockPriceList);
        }
    }
}
