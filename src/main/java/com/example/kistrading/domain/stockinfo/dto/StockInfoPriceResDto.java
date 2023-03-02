package com.example.kistrading.domain.stockinfo.dto;

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
public class StockInfoPriceResDto {

    @JsonProperty("output2")
    private List<Output2> output2;
    @JsonProperty("output1")
    private Output1 output1;
    @JsonProperty("msg_cd")
    private String msgCd;
    @JsonProperty("msg1")
    private String msg1;
    @JsonProperty("rt_cd")
    private String rtCd;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output2 {
        @JsonProperty("stck_oprc")
        private String stckOprc;
        @JsonProperty("stck_lwpr")
        private String stckLwpr;
        @JsonProperty("stck_hgpr")
        private String stckHgpr;
        @JsonProperty("stck_clpr")
        private String stckClpr;
        @JsonProperty("stck_bsop_date")
        private String stckBsopDate;
        @JsonProperty("revl_issu_reas")
        private String revlIssuReas;
        @JsonProperty("prtt_rate")
        private String prttRate;
        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;
        @JsonProperty("prdy_vrss")
        private String prdyVrss;
        @JsonProperty("mod_yn")
        private String modYn;
        @JsonProperty("flng_cls_code")
        private String flngClsCode;
        @JsonProperty("acml_vol")
        private String acmlVol;
        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output1 {
        @JsonProperty("vol_tnrt")
        private String volTnrt;
        @JsonProperty("stck_shrn_iscd")
        private String stckShrnIscd;
        @JsonProperty("stck_prpr")
        private String stckPrpr;
        @JsonProperty("stck_prdy_oprc")
        private String stckPrdyOprc;
        @JsonProperty("stck_prdy_lwpr")
        private String stckPrdyLwpr;
        @JsonProperty("stck_prdy_hgpr")
        private String stckPrdyHgpr;
        @JsonProperty("stck_prdy_clpr")
        private String stckPrdyClpr;
        @JsonProperty("stck_oprc")
        private String stckOprc;
        @JsonProperty("stck_mxpr")
        private String stckMxpr;
        @JsonProperty("stck_lwpr")
        private String stckLwpr;
        @JsonProperty("stck_llam")
        private String stckLlam;
        @JsonProperty("stck_hgpr")
        private String stckHgpr;
        @JsonProperty("stck_fcam")
        private String stckFcam;
        @JsonProperty("prdy_vrss_vol")
        private String prdyVrssVol;
        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;
        @JsonProperty("prdy_vrss")
        private String prdyVrss;
        @JsonProperty("prdy_vol")
        private String prdyVol;
        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;
        @JsonProperty("per")
        private String per;
        @JsonProperty("pbr")
        private String pbr;
        @JsonProperty("lstn_stcn")
        private String lstnStcn;
        @JsonProperty("itewhol_loan_rmnd_ratem name")
        private String itewholLoanRmndRatem;
        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;
        @JsonProperty("hts_avls")
        private String htsAvls;
        @JsonProperty("eps")
        private String eps;
        @JsonProperty("cpfn")
        private String cpfn;
        @JsonProperty("bidp")
        private String bidp;
        @JsonProperty("askp")
        private String askp;
        @JsonProperty("acml_vol")
        private String acmlVol;
        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;
    }
}
