package com.example.kistrading.domain.StockCode.entity;

import com.example.kistrading.domain.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class StockCode extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Comment("종목 이름")
    private String name;

    @Column(nullable = false)
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
    
}
