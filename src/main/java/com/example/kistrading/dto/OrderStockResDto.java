package com.example.kistrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStockResDto {

    @JsonProperty("output")
    private Output output;
    @JsonProperty("msg1")
    private String msg1;
    @JsonProperty("msg_cd")
    private String msgCd;
    @JsonProperty("rt_cd")
    private String rtCd;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output {
        @JsonProperty("ORD_TMD")
        private String ordTmd;
        @JsonProperty("ODNO")
        private String odno;
        @JsonProperty("KRX_FWDG_ORD_ORGNO")
        private String krxFwdgOrdOrgno;
    }
}
