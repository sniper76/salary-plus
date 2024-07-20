package ag.act.entity;

import java.time.LocalDateTime;

public interface UserHoldingStockBase extends ActEntity {
    int INITIAL_DISPLAY_ORDER = 100_000;

    Long getUserId();

    String getStockCode();

    Stock getStock();

    Long getQuantity();

    default Long getCashQuantity() {
        return 0L;
    }

    default Long getCreditQuantity() {
        return 0L;
    }

    default Long getSecureLoanQuantity() {
        return 0L;
    }

    default Long getPurchasePrice() {
        return 0L;
    }

    default Integer getDisplayOrder() {
        return INITIAL_DISPLAY_ORDER;
    }

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
