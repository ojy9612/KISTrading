package com.example.kistrading.domain.stockinfo.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.domain.common.service.WebClientKISConnector;
import com.example.kistrading.domain.stockcode.entity.StockCode;
import com.example.kistrading.domain.stockcode.service.StockCodeService;
import com.example.kistrading.domain.stockinfo.dto.StockInfoPriceResDto;
import com.example.kistrading.domain.stockinfo.entity.StockInfo;
import com.example.kistrading.domain.stockinfo.repository.StockInfoRepository;
import com.example.kistrading.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoService {

    private final StockInfoRepository stockInfoRepository;
    private final WebClientKISConnector<StockInfoPriceResDto> webClientKISConnectorStockInfoPriceResDto;
    private final StockCodeService stockCodeService;
    private final TokenService tokenService;

    private final PropertiesMapping pm;


    /**
     * 종목 여러개 대해 StockInfo 를 업데이트 StockCode entity에서 정보를 가져와야한다.
     *
     * @param stockCodeList 종목코드 리스트
     * @param start         일봉 데이터 시작일
     * @param end           일봉 데이터 마지막일
     */
    @Transactional
    public void upsertStockInfo(List<String> stockCodeList, LocalDate start, LocalDate end) {

        List<StockInfo> stockInfoList = stockInfoRepository.findByCodeIn(stockCodeList);
        Map<String, StockInfo> stockInfoMap = new HashMap<>();
        for (StockInfo stockInfo : stockInfoList) {
            stockInfoMap.put(stockInfo.getCode(), stockInfo);
        }

        List<StockInfo> stockInfoListForSave = new ArrayList<>();
        for (String stockCode : stockCodeList) {
            /* KIS API 를 통해 주식정보를 가져옴 */
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
            /* ----- */

            /* 정상적으로 통신했다면 db에 정보 업데이트 */
            if (response.getRtCd().equals("0")) {
                StockInfoPriceResDto.Output1 output1 = response.getOutput1();

                StockInfo stockInfo = stockInfoMap.getOrDefault(stockCode, null);

                if (stockInfo == null) {
                    stockInfoListForSave.add(StockInfo.builder()
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
                            .build());
                } else {
                    stockInfo.updateStockInfoBuilder()
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
            } else {
                throw new RuntimeException("KIS 통신 에러" + response.getRtCd() + response.getMsg1() + response.getMsgCd());
            }
            /* ----- */
        }

        stockInfoRepository.saveAll(stockInfoListForSave);
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
        Set<String> codeSet = stockCodeService.getStockCodeList().stream().map(StockCode::getCode).collect(Collectors.toSet());
        Set<String> infoSet = stockInfoRepository.findAll().stream().map(StockInfo::getCode).collect(Collectors.toSet());

        codeSet.removeAll(infoSet);

        if (codeSet.isEmpty()) {
            log.info("신규 상장된 종목이 없습니다.");
        } else {
            log.info("신규 상장된 주식 수" + codeSet.size());
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

    /**
     * stockCodeList를 이용해서 StockInfoList를 반환한다.
     *
     * @param stockCodeList stockCodeList
     * @return stockInfoList
     */
    @Transactional(readOnly = true)
    public List<StockInfo> getStockInfoList(List<String> stockCodeList) {
        return stockInfoRepository.findByCodeIn(stockCodeList);
    }
}
