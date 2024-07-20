package ag.act.module.stocksearchrecommendation;

import ag.act.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockSearchRecommendationSectionType {

    SEARCH_TREND_STOCK("SEARCH_TREND_STOCK", "검색 인기 종목"),
    STAKE_RANKING_STOCK("STAKE_RANKING_STOCK", "지분 랭킹 순위");

    private final String value;
    private final String title;

    public static StockSearchRecommendationSectionType fromValue(String value) {
        for (StockSearchRecommendationSectionType b : StockSearchRecommendationSectionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException("지원하지 않는 StockSearchRecommendationSectionType '%s' 타입입니다.".formatted(value));
    }
}
