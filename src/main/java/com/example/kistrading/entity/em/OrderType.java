package com.example.kistrading.entity.em;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum OrderType {

    BUY("BUY"),
    SELL("SELL");

    private String name;

    public static OrderType getEnum(String name) {
        for (OrderType tradeMode : values()) {
            if (tradeMode.getName().equalsIgnoreCase(name)) {
                return tradeMode;
            }
        }
        return null;
    }

    OrderType(String name) {
        this.name = name;
    }
}
