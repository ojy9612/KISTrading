package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.AccountDataResDto;
import com.example.kistrading.entity.em.TradeMode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final WebClientConnector<AccountDataResDto> webClientConnectorDto;
    private final TokenService tokenService;

    private final PropertiesMapping pm;

    public void getAccountData() {

        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        Map<String, String> reqHeader = new HashMap<>();

        String tokenString = tokenService.getAndDeleteToken();

        reqHeader.put("authorization", tokenString);
        reqHeader.put("appkey", pm.getAppKey());
        reqHeader.put("appsecret", pm.getAppSecret());
        reqHeader.put("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC8434R" : "VTTC8434R");

        reqParam.add("CANO", pm.getAccountNum());
        reqParam.add("ACNT_PRDT_CD", "01");
        reqParam.add("AFHR_FLPR_YN", "N");
        reqParam.add("OFL_YN", "");
        reqParam.add("INQR_DVSN", "01");
        reqParam.add("UNPR_DVSN", "01");
        reqParam.add("FUND_STTL_ICLD_YN", "N");
        reqParam.add("FNCG_AMT_AUTO_RDPT_YN", "N");
        reqParam.add("PRCS_DVSN", "01");
        reqParam.add("CTX_AREA_FK100", "");
        reqParam.add("CTX_AREA_NK100", "");


        List<AccountDataResDto> responseList = new ArrayList<>();

        String trCont = "F";

        while (trCont.equals("F") || trCont.equals("M")) {
            ResponseEntity<AccountDataResDto> response = webClientConnectorDto.connectIncludeHeader(HttpMethod.GET, "/uapi/domestic-stock/v1/trading/inquire-balance",
                    reqHeader, reqParam, reqBody, AccountDataResDto.class);

            List<String> tempTrCont = response.getHeaders().getOrDefault("tr_cont", Collections.singletonList(""));
            trCont = tempTrCont.get(0);

            AccountDataResDto body = response.getBody();
            responseList.add(body);

            try {
                reqParam.set("CTX_AREA_FK100", trCont.equals("F") || trCont.equals("M") ? body.getCtxAreaFk100() : "");
                reqParam.set("CTX_AREA_NK100", trCont.equals("F") || trCont.equals("M") ? body.getCtxAreaNk100() : "");
            } catch (NullPointerException e) {
                throw new NullPointerException("다음 데이터 존재하지 않습니다.");
            }

        }

        System.out.println(responseList);
    }
}
