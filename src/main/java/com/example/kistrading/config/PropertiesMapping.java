package com.example.kistrading.config;

import com.example.kistrading.domain.em.TradeMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

@Slf4j
public class PropertiesMapping {


    @Value("${kis.train.domain}")
    private String trainDomain;
    @Value("${kis.train.appkey}")
    private String trainAppkey;
    @Value("${kis.train.appsecret}")
    private String trainAppsecert;
    @Value("${kis.train.account}")
    private String trainAccountNum;

    @Value("${kis.real.domain}")
    private String realDomain;
    @Value("${kis.real.appkey}")
    private String realAppkey;
    @Value("${kis.real.appsecret}")
    private String realAppsecert;
    @Value("${kis.real.account}")
    private String realAccountNum;

    @Value("${kis.mode}")
    private String tempMode;

    @Getter
    @Value("${data.go.key.encoding}")
    private String enKey;
    @Getter
    @Value("${data.go.key.decoding}")
    private String deKey;

    @Getter
    private String domain;
    @Getter
    private String appKey;
    @Getter
    private String appSecret;
    @Getter
    private TradeMode mode;
    @Getter
    private String accountNum;


    @PostConstruct
    public void init() throws IllegalAccessException {
        mode = TradeMode.getTradeMode(tempMode);

        if (mode.equals(TradeMode.TRAIN)) {
            log.info("모의투자 계좌로 세팅 되었습니다. mode = " + mode.getName());
            domain = trainDomain;
            appKey = trainAppkey;
            appSecret = trainAppsecert;
            accountNum = trainAccountNum;
        } else if (mode.equals(TradeMode.REAL)) {
            log.info("실 계좌로 세팅 되었습니다. mode = " + mode.getName());
            domain = realDomain;
            appKey = realAppkey;
            appSecret = realAppsecert;
            accountNum = realAccountNum;
        } else {
            throw new IllegalAccessException("허용되지 않은 mode 입니다. - " + tempMode);
        }

    }

}
