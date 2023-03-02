package com.example.kistrading.domain.holiday.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResDto {

    @JsonProperty("response")
    private Response response;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("body")
        private Body body;
        @JsonProperty("header")
        private Header header;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        @JsonProperty("totalCount")
        private int totalcount;
        @JsonProperty("pageNo")
        private int pageno;
        @JsonProperty("numOfRows")
        private int numofrows;
        @JsonProperty("items")
        private Items items;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Items {
        @JsonProperty("item")
        private List<Item> item;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @JsonProperty("seq")
        private int seq;
        @JsonProperty("locdate")
        private int locdate;
        @JsonProperty("isHoliday")
        private String isholiday;
        @JsonProperty("dateName")
        private String datename;
        @JsonProperty("dateKind")
        private String datekind;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        @JsonProperty("resultMsg")
        private String resultmsg;
        @JsonProperty("resultCode")
        private String resultcode;
    }
}
