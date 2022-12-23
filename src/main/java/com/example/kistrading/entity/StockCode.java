package com.example.kistrading.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class StockCode extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private String market;

    @Builder
    public StockCode(String name, String code, String market) {
        this.name = name;
        this.code = code;
        this.market = market;
    }


}
