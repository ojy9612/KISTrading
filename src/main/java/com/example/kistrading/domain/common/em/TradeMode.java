package com.example.kistrading.domain.common.em;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum TradeMode {
    REAL("real"),
    TRAIN("train");

    private String name;

    public static TradeMode getTradeMode(String name) {
        for (TradeMode tradeMode : values()) {
            if (tradeMode.getName().equalsIgnoreCase(name)) {
                return tradeMode;
            }
        }
        throw new IllegalStateException("유효하지 않은 TradeMode 입니다. - " + name);
    }

    TradeMode(String name) {
        this.name = name;
    }
}
