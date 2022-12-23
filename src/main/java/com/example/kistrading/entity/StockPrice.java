package com.example.kistrading.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StockPrice extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("주식 이름")
    private String name;

    @Column(nullable = false)
    @Comment("주식 코드")
    private String code;

    @Column(nullable = false)
    @Comment("일자")
    private LocalDateTime date;

    @Column(nullable = false)
    @Comment("종가")
    private BigDecimal closePrice;

    @Column(nullable = false)
    @Comment("시가")
    private BigDecimal openPrice;

    @Column(nullable = false)
    @Comment("고가")
    private BigDecimal highPrice;

    @Column(nullable = false)
    @Comment("저가")
    private BigDecimal lowPrice;

    @Column(nullable = false)
    @Comment("거래량")
    private BigDecimal volume;

    @Column(nullable = false)
    @Comment("거래 대금")
    private BigDecimal volumeTotalPrice;

    @Column
    @Comment("전일 대비 상승폭")
    private BigDecimal beforeGapPrice;

    @Column
    @Comment("전일 대비 기호")
    private String beforeGapPriceSign;

    @Builder
    public StockPrice(String name, String code, LocalDateTime date, BigDecimal closePrice, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal volume, BigDecimal volumeTotalPrice, BigDecimal beforeGapPrice, String beforeGapPriceSign) {
        this.name = name;
        this.code = code;
        this.date = date;
        this.closePrice = closePrice;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.volumeTotalPrice = volumeTotalPrice;
        this.beforeGapPrice = beforeGapPrice;
        this.beforeGapPriceSign = beforeGapPriceSign;
    }
}
