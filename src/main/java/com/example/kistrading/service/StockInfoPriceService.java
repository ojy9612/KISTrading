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
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StockInfoPriceService {

    private final WebClientConnector<StockInfoPriceResDto> webClientConnectorStockInfoPriceResDto;
    private final WebClient webClient;
    private final TokenService tokenService;
    private final StockInfoRepository stockInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockCodeRepository stockCodeRepository;

    private final PropertiesMapping pm;

    @Transactional
    public void getAllStockInfoPrice() { // 시작 날짜 기준으로 최신 값으로 채우기
        LocalDateTime now;
        if (LocalTime.now().compareTo(LocalTime.of(16, 0)) < 0) {
            now = LocalDateTime.now().minusDays(1);
        } else {
            now = LocalDateTime.now();
        }

        List<StockInfo> stockInfoList = stockInfoRepository.findAll();


        for (StockInfo stockInfo : stockInfoList) {
            List<StockPrice> stockPriceList = stockInfo.getStockPriceList();
            LocalDateTime date;
            long delta;
            if (!stockPriceList.isEmpty()) {
                date = stockPriceList.get(0).getDate();

                delta = Duration.between(date, now).toDays();

                if (delta <= 0) {
                    continue;
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

                createStockInfoPrice(stockInfo.getCode(), start, end);
            }
        }
    }

    @Transactional
    public void createStockInfoPrice(String stockCode, LocalDateTime start, LocalDateTime end) {

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

        StockInfoPriceResDto response = webClientConnectorStockInfoPriceResDto.connect(HttpMethod.GET, "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice",
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


            System.out.println(stockInfo.getName());

            List<StockPrice> stockPriceList = new ArrayList<>();

            for (StockInfoPriceResDto.Output2 output2 : response.getOutput2()) {
                if (output2.getStckBsopDate() == null) {
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
        }
    }

    @Transactional
    public void checkNewStock() {
        int codeSize = stockCodeRepository.findAll().size();
        int infoSize = stockInfoRepository.findAll().size();

        if (codeSize != infoSize) {

        }
    }

    @Transactional
    public void createUpdateStockCode() { // 하드 코딩.. KIS가 종목코드 업데이트 해주길 기다립시다..
        String url = "https://apis.data.go.kr/1160100/service/GetKrxListedInfoService/getItemInfo?" +
                "serviceKey=" + pm.getEnKey();

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient copyWebClient = webClient.mutate()
                .clientConnector(   // ssl 접속 허용..
                        new ReactorClientHttpConnector(
                                HttpClient.create().secure(t -> {
                                    try {
                                        t.sslContext(SslContextBuilder
                                                .forClient()
                                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                                .build());
                                    } catch (SSLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        ))
                .uriBuilderFactory(factory)
                .baseUrl(url)
                .build();

        int pageNo = 1;
        int totalCount = Integer.MAX_VALUE;
        List<StockCodeResDto.Item> bodyList = new ArrayList<>();
        ResponseEntity<StockCodeResDto> response;
        String beforeDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        while (totalCount > 1000 * (pageNo - 1)) {
            int finalPageNo = pageNo;
            response = copyWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("")
                            .queryParam("pageNo", finalPageNo)
                            .queryParam("resultType", "json")
                            .queryParam("numOfRows", "1000")
                            .queryParam("basDt", beforeDate)
                            .build())
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(StockCodeResDto.class))
                    .block();

            totalCount = response.getBody().getResponse().getBody().getTotalcount();
            pageNo++;
            bodyList.addAll(response.getBody().getResponse().getBody().getItems().getItem());
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
                if (code.getCode().equals(stockCode.getCode())
                        && code.getName().equals(stockCode.getName())
                        && code.getMarket().equals(stockCode.getName())) {
                    return false;
                }
            }
            return true;
        }).toList();

        stockCodeRepository.saveAll(newStockCodeList);
    }

}
