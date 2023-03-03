package com.example.kistrading.domain.stockprice.entity;

import com.example.kistrading.domain.TimeStamped;
import com.example.kistrading.domain.stockinfo.entity.StockInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "stock_price", indexes = {
        @Index(name = "date_index", columnList = "date")
})
public class StockPrice extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("일자")
    private LocalDate date;

    @Column(nullable = false, precision = 12)
    @Comment("종가")
    private BigDecimal closePrice;

    @Column(nullable = false, precision = 12)
    @Comment("시가")
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 12)
    @Comment("고가")
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 12)
    @Comment("저가")
    private BigDecimal lowPrice;

    @Column(nullable = false)
    @Comment("거래량")
    private Long volume;

    @Column(precision = 15)
    @Comment("거래 대금")
    private BigDecimal volumeTotalPrice;

    @Column(precision = 12)
    @Comment("전일 대비 상승폭")
    private BigDecimal beforeGapPrice;

    @Column
    @Comment("전일 대비 기호")
    private String beforeGapPriceSign;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockInfo stockInfo;

    public void registerStockInfo(StockInfo stockInfo) {
        this.stockInfo = stockInfo;
    }

    @Builder
    public StockPrice(LocalDate date, BigDecimal closePrice, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, Long volume, BigDecimal volumeTotalPrice, BigDecimal beforeGapPrice, String beforeGapPriceSign, StockInfo stockInfo) {
        this.date = date;
        this.closePrice = closePrice;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.volumeTotalPrice = volumeTotalPrice;
        this.beforeGapPrice = beforeGapPrice;
        this.beforeGapPriceSign = beforeGapPriceSign;
        stockInfo.addStockPrice(this);
    }

    @Builder(builderMethodName = "updateStockPriceBuilder")
    public void updateStockPrice(LocalDate date, BigDecimal closePrice, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, Long volume, BigDecimal volumeTotalPrice, BigDecimal beforeGapPrice, String beforeGapPriceSign, StockInfo stockInfo) {
        this.date = date;
        this.closePrice = closePrice;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.volumeTotalPrice = volumeTotalPrice;
        this.beforeGapPrice = beforeGapPrice;
        this.beforeGapPriceSign = beforeGapPriceSign;
        stockInfo.addStockPrice(this);
    }


}
