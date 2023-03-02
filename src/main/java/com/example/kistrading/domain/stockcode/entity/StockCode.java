package com.example.kistrading.domain.stockcode.entity;

import com.example.kistrading.domain.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "stock_code", uniqueConstraints = {
        @UniqueConstraint(name = "UK_stock_info_name", columnNames = {"name"}),
        @UniqueConstraint(name = "UK_stock_info_code", columnNames = {"code"})
}, indexes = {
        @Index(name = "code_index", columnList = "code")
})
public class StockCode extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @Comment("종목 이름")
    private String name;

    @Column(nullable = false, unique = true)
    @Comment("종목 코드")
    private String code;

    @Column(nullable = false)
    @Comment("시장 이름 (ex. 코스피, 코스닥, 코넥스)")
    private String market;

    @Builder
    public StockCode(String name, String code, String market) {
        this.name = name;
        this.code = code;
        this.market = market;
    }

    @Builder(builderMethodName = "updateStockCodeBuilder")
    public void updateStockCode(String market) {
        this.market = market;
    }

}
