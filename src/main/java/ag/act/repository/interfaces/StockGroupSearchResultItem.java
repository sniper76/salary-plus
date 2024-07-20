package ag.act.repository.interfaces;

import java.time.LocalDateTime;

public interface StockGroupSearchResultItem {

    Long getId();

    String getName();

    String getDescription();

    ag.act.model.Status getStatus();

    Integer getStockCount();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    LocalDateTime getDeletedAt();
}
