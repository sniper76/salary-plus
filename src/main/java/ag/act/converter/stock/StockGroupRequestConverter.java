package ag.act.converter.stock;

import ag.act.entity.StockGroup;
import ag.act.model.CreateStockGroupRequest;
import ag.act.model.Status;
import ag.act.model.UpdateStockGroupRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StockGroupRequestConverter {

    public StockGroup convert(CreateStockGroupRequest createStockGroupRequest) {
        return StockGroup.builder()
            .name(createStockGroupRequest.getName())
            .description(createStockGroupRequest.getDescription())
            .status(Status.ACTIVE)
            .build();
    }

    public StockGroup convert(Long stockGroupId, UpdateStockGroupRequest createStockGroupRequest) {
        return StockGroup.builder()
            .id(stockGroupId)
            .name(createStockGroupRequest.getName())
            .description(createStockGroupRequest.getDescription())
            .status(Status.ACTIVE)
            .build();
    }

    public StockGroup map(UpdateStockGroupRequest updateStockGroupRequest, StockGroup stockGroup) {

        if (StringUtils.isNotBlank(updateStockGroupRequest.getName())) {
            stockGroup.setName(updateStockGroupRequest.getName().trim());
        }

        stockGroup.setDescription(Optional.ofNullable(updateStockGroupRequest.getDescription()).map(String::trim).orElse(null));

        return stockGroup;
    }
}
