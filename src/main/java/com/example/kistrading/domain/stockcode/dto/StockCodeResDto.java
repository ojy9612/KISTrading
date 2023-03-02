package com.example.kistrading.domain.stockcode.dto;

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
public class StockCodeResDto {

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
        @JsonProperty("items")
        private Items items;
        @JsonProperty("totalCount")
        private int totalcount;
        @JsonProperty("pageNo")
        private int pageno;
        @JsonProperty("numOfRows")
        private int numofrows;
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
        @JsonProperty("corpNm")
        private String corpnm;
        @JsonProperty("crno")
        private String crno;
        @JsonProperty("itmsNm")
        private String itmsnm;
        @JsonProperty("mrktCtg")
        private String mrktctg;
        @JsonProperty("isinCd")
        private String isincd;
        @JsonProperty("srtnCd")
        private String srtncd;
        @JsonProperty("basDt")
        private String basdt;
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
