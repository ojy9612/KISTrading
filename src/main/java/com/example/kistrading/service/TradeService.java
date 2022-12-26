package com.example.kistrading.service;

import com.example.kistrading.config.PropertiesMapping;
import com.example.kistrading.dto.OrderStockResDto;
import com.example.kistrading.entity.em.OrderType;
import com.example.kistrading.entity.em.TradeMode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final WebClientKISConnector<OrderStockResDto> webClientKISConnectorDto;
    private final TokenService tokenService;

    private final PropertiesMapping pm;

    @Transactional
    public void orderStock(OrderType orderType, String stockCode,
                           String orderPrice, String orderAmount) {

        Map<String, String> reqBody = new HashMap<>();
        Map<String, String> reqHeader = new HashMap<>();

        reqHeader.put("authorization", pm.checkGetToken());
        reqHeader.put("appkey", pm.getAppKey());
        reqHeader.put("appsecret", pm.getAppSecret());
        if (orderType.getName().equals("BUY")) {
            reqHeader.put("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC0802U" : "VTTC0802U");
        } else if (orderType.getName().equals("SELL")) {
            reqHeader.put("tr_id", pm.getMode().equals(TradeMode.REAL) ? "TTTC0801U" : "VTTC0801U");
        }

        reqBody.put("CANO", pm.getAccountNum());
        reqBody.put("ACNT_PRDT_CD", "01");
        reqBody.put("PDNO", stockCode);
        reqBody.put("ORD_DVSN", orderPrice.equals("0") ? "01" : "00");
        reqBody.put("ORD_QTY", orderAmount);
        reqBody.put("ORD_UNPR", orderPrice);

        OrderStockResDto response = webClientKISConnectorDto.connect(HttpMethod.POST, "/uapi/domestic-stock/v1/trading/order-cash",
                reqHeader, null, reqBody, OrderStockResDto.class);


        System.out.println(response.getRtCd() + response.getMsgCd() + response.getMsg1());
    }

}
