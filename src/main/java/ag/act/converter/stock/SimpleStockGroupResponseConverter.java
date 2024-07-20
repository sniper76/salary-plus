package ag.act.converter.stock;


import ag.act.converter.Converter;
import ag.act.dto.stock.SimpleStockGroupDto;
import org.springframework.stereotype.Component;

@Component
public class SimpleStockGroupResponseConverter implements Converter<SimpleStockGroupDto, ag.act.model.SimpleStockGroupResponse> {

    public ag.act.model.SimpleStockGroupResponse convert(SimpleStockGroupDto stock) {
        return new ag.act.model.SimpleStockGroupResponse()
            .id(stock.getId())
            .name(stock.getName());
    }

    @Override
    public ag.act.model.SimpleStockGroupResponse apply(SimpleStockGroupDto stock) {
        return convert(stock);
    }
}
