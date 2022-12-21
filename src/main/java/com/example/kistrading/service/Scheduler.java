package com.example.kistrading.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    /* 초 분 시 일 월 요일, [참고문서:https://zamezzz.tistory.com/197] */
    @Scheduled(cron = "0 0 8 * * ?") // 매일 오전 8시에 실행
    public void aaaa(){

    }
}
