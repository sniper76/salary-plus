package ag.act.dto.krx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings({"AbbreviationAsWordInName", "SpellCheckingInspection", "checkstyle:MemberName"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItemDto {
    private String ISU_CD; // 표준코드
    private String ISU_SRT_CD; // 단축코드
    private String ISU_NM; // 한글 종목명
    private String ISU_ABBRV; // 한글 종목약명
    private String ISU_ENG_NM; // 영문 종목명
    private String LIST_DD; // 상장일
    private String MKT_TP_NM; // 시장구분
    private String SECUGRP_NM; // 증권구분
    private String SECT_TP_NM; // 소속부
    private String KIND_STKCERT_TP_NM; // 주식종류
    private String PARVAL; // 액면가
    private String LIST_SHRS; // 상장주식수
}
