package com.example.kistrading.service;

import com.example.kistrading.dto.HolidayResDto;
import com.example.kistrading.entity.Holiday;
import com.example.kistrading.repository.HolidayRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final WebClientDataGoKrConnector<HolidayResDto> webClientDataGoKrConnectorHolidayResDto;
    private final HolidayRepository holidayRepository;

    @Getter
    private LocalDateTime nowDate;
    @Getter
    private LocalDateTime availableDate;


    @PostConstruct
    @Transactional
    public LocalDateTime checkAvailableDate() {
        if (LocalTime.now().compareTo(LocalTime.of(16, 0)) < 0) {
            nowDate = LocalDateTime.now().minusDays(1);
        } else {
            nowDate = LocalDateTime.now();
        }

        availableDate = nowDate;
        while (this.isHoliday(availableDate)) {
            availableDate = availableDate.minusDays(1);
        }

        return availableDate;
    }

    /**
     * 공휴일과 주말을 계산해 DB에 데이터를 생성한다.
     *
     * @param year 년도
     */
    @Transactional
    public void createHolidaysByYear(int year) {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();
        reqParam.set("solYear", String.valueOf(year));
        reqParam.set("_type", "json");
        reqParam.set("numOfRows", "100");

        HolidayResDto response = webClientDataGoKrConnectorHolidayResDto.connect(HttpMethod.GET,
                "B090041/openapi/service/SpcdeInfoService/getRestDeInfo",
                null,
                reqParam,
                null,
                HolidayResDto.class);

        for (HolidayResDto.Item item : response.getResponse().getBody().getItems().getItem()) {
            if (item.getIsholiday().equals("Y")) {
                this.createHoliday(Holiday.builder()
                        .name(item.getDatename())
                        .date(LocalDate.parse(String.valueOf(item.getLocdate()), DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay())
                        .build());
            }

        }

        LocalDate date = LocalDate.of(year, 1, 1);

        while (!date.equals(LocalDate.of(year + 1, 1, 1))) {
            if (date.getDayOfWeek().getValue() == DayOfWeek.SUNDAY.getValue()
                    || date.getDayOfWeek().getValue() == DayOfWeek.SATURDAY.getValue()) {
                this.createHoliday(Holiday.builder()
                        .name(date.getDayOfWeek().name())
                        .date(date.atStartOfDay())
                        .build());
            }

            date = date.plusDays(1);
        }
    }

    /**
     * 공휴일이 이미 DB에 있는지 검사 후 생성한다.
     *
     * @param holiday 저장할 Holiday 객체
     */
    @Transactional
    public void createHoliday(Holiday holiday) {
        if (!this.isHoliday(holiday.getDate())) {
            holidayRepository.save(holiday);
        }
    }

    /**
     * 입력한 날짜가 공휴일인지 검사한다.
     *
     * @param date 검사할 날짜
     * @return boolean
     */
    @Transactional(readOnly = true)
    public boolean isHoliday(LocalDateTime date) {
        return holidayRepository.findByDate(date).isPresent();
    }

}
