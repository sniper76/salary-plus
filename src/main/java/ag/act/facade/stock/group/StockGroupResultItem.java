package ag.act.facade.stock.group;

import ag.act.entity.StockGroup;
import ag.act.model.Status;
import ag.act.repository.interfaces.StockGroupSearchResultItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class StockGroupResultItem implements StockGroupSearchResultItem {

    private StockGroup stockGroup;
    private Integer stockCount;

    @Override
    public Long getId() {
        return stockGroup.getId();
    }

    @Override
    public String getName() {
        return stockGroup.getName();
    }

    @Override
    public String getDescription() {
        return stockGroup.getDescription();
    }

    @Override
    public Status getStatus() {
        return stockGroup.getStatus();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return stockGroup.getCreatedAt();
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return stockGroup.getUpdatedAt();
    }

    @Override
    public LocalDateTime getDeletedAt() {
        return stockGroup.getDeletedAt();
    }
}
