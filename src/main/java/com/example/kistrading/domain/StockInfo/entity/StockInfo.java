package com.example.kistrading.domain.StockInfo.entity;

import com.example.kistrading.domain.StockPrice.entity.StockPrice;
import com.example.kistrading.domain.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "stock_info", uniqueConstraints = {
        @UniqueConstraint(name = "UK_stock_info_name", columnNames = {"name"}),
        @UniqueConstraint(name = "UK_stock_info_code", columnNames = {"code"})
})
public class StockInfo extends TimeStamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("주식 이름")
    private String name;

    @Column(nullable = false, unique = true)
    @Comment("주식 코드")
    private String code;

    @Column
    @Comment("주식 단축코드")
    private String otherCode;

    @Column
    @Comment("액면가")
    private String fcam;

    @Column(nullable = false)
    @Comment("상장 주수")
    private Long amount;

    @Column(nullable = false)
    @Comment("시가 총액")
    private String marketCapitalization;

    @Column(nullable = false)
    @Comment("자본금")
    private String capital;

    @Column(nullable = false)
    @Comment("PER")
    private String per;

    @Column(nullable = false)
    @Comment("PBR")
    private String pbr;

    @Column
    @Comment("EPS")
    private String eps;

    @OneToMany(mappedBy = "stockInfo", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("date desc ")
    private final List<StockPrice> stockPriceList = new ArrayList<>();

    public void addStockPrice(StockPrice stockPrice) {
        stockPrice.registerStockInfo(this);
        this.stockPriceList.add(stockPrice);
    }

    @Builder
    public StockInfo(String name, String code, String otherCode, String fcam, Long amount, String marketCapitalization, String capital, String per, String pbr, String eps) {
        this.name = name;
        this.code = code;
        this.otherCode = otherCode;
        this.fcam = fcam;
        this.amount = amount;
        this.marketCapitalization = marketCapitalization;
        this.capital = capital;
        this.per = per;
        this.pbr = pbr;
        this.eps = eps;
    }

    @Builder(builderMethodName = "updateStockInfoBuilder")
    public void updateStockInfo(String otherCode, String fcam, Long amount, String marketCapitalization, String capital, String per, String pbr, String eps) {
        this.otherCode = otherCode;
        this.fcam = fcam;
        this.amount = amount;
        this.marketCapitalization = marketCapitalization;
        this.capital = capital;
        this.per = per;
        this.pbr = pbr;
        this.eps = eps;
    }


}
