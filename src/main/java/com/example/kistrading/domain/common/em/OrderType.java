package com.example.kistrading.domain.common.em;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public enum OrderType {

    BUY("BUY"),
    SELL("SELL");

    private String name;

    public static @Nullable OrderType getEnum(String name) {
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
