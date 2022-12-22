package com.example.kistrading.entity.em;

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
        return null;
    }

    TradeMode(String name) {
        this.name = name;
    }
}
