package ag.act.dto.mydata;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public enum MyDataProductType {
    DOMESTIC_STOCK("401", "국내주식", "ISIN CODE", "주식, 리츠, 수익증권, 코넥스, K-OTC, 장외주식 기타, ETF, ELW, ETN 등"),
    FOREIGN_STOCK("402", "해외주식", "ISIN CODE", "미국, 홍콩, 중국, 해외ETF, 해외DR 등"),
    DOMESTIC_BOND("403", "국내채권", "ISIN CODE", "국채, 지방채, 회사채, 통안채 등"),
    FOREIGN_BOND("404", "해외채권", "ISIN CODE", "해외국채, 해외금융채, 해외회사채 등"),
    LOAN_BOND("405", "대출채권", "ISIN CODE", "기업금융, PF, CP, 사모사채 등"),
    DOMESTIC_DERIVATIVE("406", "국내파생결합증권", "ISIN CODE", "ELS, DLS, ELB, DLB 등"),
    FOREIGN_DERIVATIVE("407", "해외파생결합증권", "ISIN CODE", "해외ELS, 해외DLS, 해외ELB, 해외DLB 등"),
    DOMESTIC_ONEX_DERIVATIVE("408", "국내장내파생상품", "ISIN CODE", "주가지수선물, 주가지수옵션 등"),
    FOREIGN_ONEX_DERIVATIVE("409", "해외장내파생상품", "자체코드", "해외 지수선물, 해외 지수옵션 등"),
    OTHER_DERIVATIVE("410", "기타파생상품", "자체코드", "장외파생, 외부선물사장내파생 등"),
    DOMESTIC_SHORT_TERM("411", "국내단기금융상품", "ISIN CODE", "CD, CP, 전단채, RP, REPO, 발행어음, 소매채권 등"),
    FOREIGN_SHORT_TERM("412", "해외단기금융상품", "ISIN CODE", "해외CD, 해외대고객RP 등"),
    DOMESTIC_FUND("413", "국내펀드", "협회표준코드", "국내 집합투자증권(MMF, 주식형, 혼합자산형 등)"),
    FOREIGN_DOMESTIC_FUND("414", "해외(역내)펀드", "협회표준코드", "해외(역내) 집합투자증권(주식형, 채권형, 재간접형 등)"),
    FOREIGN_FOREIGN_FUND("415", "해외(역외)펀드", "자체코드", "해외(역외) 집합투자증권(주식형, 채권형, 재간접형 등)"),
    WRAP("416", "랩", "자체코드", "주식운용형, 자문운용형, 채권운용형 등"),
    TRUST("417", "신탁", "자체코드", "특정금전신탁, 재산신탁 등"),
    CASH_AND_DEPOSIT("418", "현금과 예금", "자체코드", "MMDA, CMA, 보통예금 등"),
    SPOT("420", "현물", "ISIN CODE", "금현물 등"),
    OTHER("421", "기타", "자체코드", "위 제외 기타 모든 상품");

    @Getter
    private final String displayName;
    @Getter
    private final String prodCode;
    private final String codeType;
    private final String description;

    MyDataProductType(String prodCode, String displayName, String codeType, String description) {
        this.displayName = displayName;
        this.prodCode = prodCode;
        this.codeType = codeType;
        this.description = description;
    }

    private static final Map<String, Boolean> supportCodeMap = new HashMap<>() {
        {
            put(MyDataProductType.DOMESTIC_STOCK.getProdCode(), Boolean.TRUE);
            put(MyDataProductType.OTHER.getProdCode(), Boolean.TRUE);
        }
    };

    public static boolean isSupportedProdCode(String code) {
        return supportCodeMap.getOrDefault(code, Boolean.FALSE);
    }
}
