package com.example.kistrading.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length =  500, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredDate;

    @Builder
    public Token(String token, LocalDateTime expiredDate) {
        this.token = token;
        this.expiredDate = expiredDate;
    }
}
