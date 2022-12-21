package com.example.kistrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccountDataResDto {
    @JsonProperty("msg1")
    private String msg1;
    @JsonProperty("msg_cd")
    private String msgCd;
    @JsonProperty("rt_cd")
    private String rtCd;
    @JsonProperty("output2")
    private List<AccountInfo> accountInfos;
    @JsonProperty("output1")
    private List<StockInfo> stockInfos;
    @JsonProperty("ctx_area_nk100")
    private String ctxAreaNk100;
    @JsonProperty("ctx_area_fk100")
    private String ctxAreaFk100;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockInfo {
        @JsonProperty("pdno")
        private String stockCode;
        @JsonProperty("prdt_name")
        private String stockName;
        @JsonProperty("trad_dvsn_name")
        private String tradDvsn;
        @JsonProperty("bfdy_buy_qty")
        private String bfdyBuy;
        @JsonProperty("bfdy_sll_qty")
        private String bfdySell;
        @JsonProperty("thdt_buyqty")
        private String nedyBuy;
        @JsonProperty("thdt_sll_qty")
        private String nedySell;
        @JsonProperty("hldg_qty")
        private String amount;
        @JsonProperty("ord_psbl_qty")
        private String canOrderAmount;
        @JsonProperty("pchs_avg_pric")
        private String avgPrice;
        @JsonProperty("pchs_amt")
        private String personalTotalBuyPrice;
        @JsonProperty("prpr")
        private String nowPrice;
        @JsonProperty("evlu_amt")
        private String evluPrice;
        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmount;
        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRate;
        @JsonProperty("evlu_erng_rt")
        private String evluEarnRate;
        @JsonProperty("fltt_rt")
        private String rate;
        @JsonProperty("bfdy_cprs_icdc")
        private String bfdyRate;
        @JsonProperty("item_mgna_rt_name")
        private String stockMgnaName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountInfo {
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
