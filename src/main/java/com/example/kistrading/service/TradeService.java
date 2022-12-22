package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.OrderStockResDto;
import com.example.kistrading.entity.em.OrderType;
import com.example.kistrading.entity.em.TradeMode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final WebClientConnector<OrderStockResDto> webClientConnectorDto;
    private final WebClientConnector<String> webClientConnectorString;
    private final TokenService tokenService;

    private final PropertiesMapping pm;

    @Transactional
    public void OrderStock(OrderType orderType, String stockCode,
                           String orderPrice, String orderAmount) {

        Map<String, String> reqBody = new HashMap<>();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();

        reqHeader.add("authorization", tokenService.getAndDeleteToken());
        reqHeader.add("appkey", pm.getAppKey());
        reqHeader.add("appsecret", pm.getAppSecret());
        if (orderType.getName().equals("BUY")) {
            reqHeader.add("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC0802U" : "VTTC0802U");
        } else if (orderType.getName().equals("SELL")) {
            reqHeader.add("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC0801U" : "VTTC0801U");
        }

        reqBody.put("CANO", pm.getAccountNum());
        reqBody.put("ACNT_PRDT_CD", "01");
        reqBody.put("PDNO", stockCode);
        reqBody.put("ORD_DVSN", orderPrice.equals("0") ? "01" : "00");
        reqBody.put("ORD_QTY", orderAmount);
        reqBody.put("ORD_UNPR", orderPrice);

//        OrderStockResDto response = webClientConnectorDto.connect(HttpMethod.POST, "/uapi/domestic-stock/v1/trading/order-cash",
//                reqHeader, null, reqBody, OrderStockResDto.class);
        String asd = webClientConnectorString.connect(HttpMethod.POST, "/uapi/domestic-stock/v1/trading/order-cash",
                reqHeader, null, reqBody, String.class);

//        System.out.println(response);
        System.out.println(asd);
    }

}
