package ag.act.itutil.holder;

import ag.act.entity.StockGroup;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockGroupsTestHolder {

    private boolean isInitialized = false;

    public void initialize(List<StockGroup> stockList) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        stockGroups.addAll(stockList);
    }

    private final List<StockGroup> stockGroups = new ArrayList<>();

    public StockGroup addOrSet(@NotNull StockGroup stockGroup) {
        stockGroups.stream()
            .filter(s -> s.getId().equals(stockGroup.getId()))
            .findFirst()
            .ifPresentOrElse(
                s -> stockGroups.set(stockGroups.indexOf(s), stockGroup),
                () -> stockGroups.add(stockGroup)
            );

        return stockGroup;
    }

    public List<StockGroup> getStockGroups() {
        return Collections.unmodifiableList(stockGroups);
    }
}
