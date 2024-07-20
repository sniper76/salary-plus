package ag.act.configuration.initial.caching;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public enum CachingType {
    @FieldNameConstants.Include USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS("stock reference dates"),
    @FieldNameConstants.Include STOCK_SEARCH_RECOMMENDATION_SECTIONS("stock search recommendations"),
    ;

    private final String description;

}
