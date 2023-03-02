package com.example.kistrading.domain.holiday.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "holiday", indexes = {
        @Index(name = "date_index", columnList = "date")
})
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Comment("공휴일 이름")
    private String name;

    @Column(nullable = false)
    @Comment("공휴일 날짜")
    private LocalDate date;

    @Builder
    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }
}
