package ag.act.dto.krx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings({"AbbreviationAsWordInName","SpellCheckingInspection","checkstyle:MemberName"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StkItemPriceDto {
    private String BAS_DD;//기준일자
    private String ISU_CD;//표준코드
    private String ISU_NM;//한글 종목명
    private String MKT_NM;//시장구분
    private String SECT_TP_NM;//소속부
    private String TDD_CLSPRC;//종가
    private String CMPPREVDD_PRC;//대비
    private String FLUC_RT;//등략률
    private String TDD_OPNPRC;//시가
    private String TDD_HGPRC;//고가
    private String TDD_LWPRC;//저가
    private String ACC_TRDVOL;//거래량
    private String ACC_TRDVAL;//거래대금
    private String MKTCAP;//시가총액
    private String LIST_SHRS;//상장주식수
}
