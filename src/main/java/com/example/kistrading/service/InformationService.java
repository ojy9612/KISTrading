package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.AccountDataResDto;
import com.example.kistrading.entity.em.TradeMode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InformationService {

    private final WebClientConnector<AccountDataResDto> webClientConnectorDto;
    private final TokenService tokenService;

    private final PropertiesMapping pm;

    public void getAccountData() {

        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();

        String tokenString = tokenService.getAndDeleteToken();

        reqHeader.add("authorization", tokenString);
        reqHeader.add("appkey", pm.getAppKey());
        reqHeader.add("appsecret", pm.getAppSecret());
        reqHeader.add("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC8434R" : "VTTC8434R");

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


        AccountDataResDto connect = webClientConnectorDto.connect(HttpMethod.GET, "/uapi/domestic-stock/v1/trading/inquire-balance",
                reqHeader, reqParam, reqBody, AccountDataResDto.class);

        System.out.println(connect);
    }
}
