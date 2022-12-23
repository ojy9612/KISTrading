package com.example.kistrading.entity;


import com.example.kistrading.entity.em.TradeMode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Token extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String tokenValue;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TradeMode mode;

    @Column(nullable = false)
    private LocalDateTime expiredDate;


    @Builder
    public Token(String tokenValue, TradeMode mode, LocalDateTime expiredDate) {
        this.tokenValue = tokenValue;
        this.mode = mode;
        this.expiredDate = expiredDate;
    }
}
