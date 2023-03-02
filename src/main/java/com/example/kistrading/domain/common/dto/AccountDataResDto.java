package com.example.kistrading.domain.common.dto;

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
public class AccountDataResDto {
    @JsonProperty("msg1")
    private String msg1;
    @JsonProperty("msg_cd")
    private String msgCd;
    @JsonProperty("rt_cd")
    private String rtCd;
    @JsonProperty("output2")
    private List<Output2> output2s;
    @JsonProperty("output1")
    private List<Output1> output1s;
    @JsonProperty("ctx_area_nk100")
    private String ctxAreaNk100;
    @JsonProperty("ctx_area_fk100")
    private String ctxAreaFk100;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output1 {
        @JsonProperty("stck_loan_unpr")
        private String stckLoanUnpr;
        @JsonProperty("sbst_pric")
        private String sbstPric;
        @JsonProperty("grta_rt_name")
        private String grtaRtName;
        @JsonProperty("item_mgna_rt_name")
        private String itemMgnaRtName;
        @JsonProperty("bfdy_cprs_icdc")
        private String bfdyCprsIcdc;
        @JsonProperty("fltt_rt")
        private String flttRt;
        @JsonProperty("expd_dt")
        private String expdDt;
        @JsonProperty("stln_slng_chgs")
        private String stlnSlngChgs;
        @JsonProperty("loan_amt")
        private String loanAmt;
        @JsonProperty("loan_dt")
        private String loanDt;
        @JsonProperty("evlu_erng_rt")
        private String evluErngRt;
        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRt;
        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmt;
        @JsonProperty("evlu_amt")
        private String evluAmt;
        @JsonProperty("prpr")
        private String prpr;
        @JsonProperty("pchs_amt")
        private String pchsAmt;
        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric;
        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty;
        @JsonProperty("hldg_qty")
        private String hldgQty;
        @JsonProperty("thdt_sll_qty")
        private String thdtSllQty;
        @JsonProperty("thdt_buyqty")
        private String thdtBuyqty;
        @JsonProperty("bfdy_sll_qty")
        private String bfdySllQty;
        @JsonProperty("bfdy_buy_qty")
        private String bfdyBuyQty;
        @JsonProperty("trad_dvsn_name")
        private String tradDvsnName;
        @JsonProperty("prdt_name")
        private String prdtName;
        @JsonProperty("pdno")
        private String pdno;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output2 {
        @JsonProperty("asst_icdc_erng_rt")
        private String asstIcdcErngRt;
        @JsonProperty("asst_icdc_amt")
        private String asstIcdcAmt;
        @JsonProperty("bfdy_tot_asst_evlu_amt")
        private String bfdyTotAsstEvluAmt;
        @JsonProperty("tot_stln_slng_chgs")
        private String totStlnSlngChgs;
        @JsonProperty("evlu_pfls_smtl_amt")
        private String evluPflsSmtlAmt;
        @JsonProperty("evlu_amt_smtl_amt")
        private String evluAmtSmtlAmt;
        @JsonProperty("pchs_amt_smtl_amt")
        private String pchsAmtSmtlAmt;
        @JsonProperty("fncg_gld_auto_rdpt_yn")
        private String fncgGldAutoRdptYn;
        @JsonProperty("nass_amt")
        private String nassAmt;
        @JsonProperty("tot_evlu_amt")
        private String totEvluAmt;
        @JsonProperty("scts_evlu_amt")
        private String sctsEvluAmt;
        @JsonProperty("tot_loan_amt")
        private String totLoanAmt;
        @JsonProperty("thdt_tlex_amt")
        private String thdtTlexAmt;
        @JsonProperty("bfdy_tlex_amt")
        private String bfdyTlexAmt;
        @JsonProperty("d2_auto_rdpt_amt")
        private String d2AutoRdptAmt;
        @JsonProperty("thdt_sll_amt")
        private String thdtSllAmt;
        @JsonProperty("bfdy_sll_amt")
        private String bfdySllAmt;
        @JsonProperty("nxdy_auto_rdpt_amt")
        private String nxdyAutoRdptAmt;
        @JsonProperty("thdt_buy_amt")
        private String thdtBuyAmt;
        @JsonProperty("bfdy_buy_amt")
        private String bfdyBuyAmt;
        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt;
        @JsonProperty("prvs_rcdl_excc_amt")
        private String prvsRcdlExccAmt;
        @JsonProperty("nxdy_excc_amt")
        private String nxdyExccAmt;
        @JsonProperty("dnca_tot_amt")
        private String dncaTotAmt;
    }

}
