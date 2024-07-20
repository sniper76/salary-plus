package ag.act.converter.home;

import ag.act.dto.stock.home.ConvertStockHomeSectionResponseDto;
import ag.act.model.ListSectionHeaderResponse;
import ag.act.model.StockHomeSectionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockHomeSectionResponseConverter {
    public ag.act.model.StockHomeSectionResponse convert(List<ag.act.model.CarouselItemResponse> carouselItems) {
        return new StockHomeSectionResponse()
            .type("carousel")
            .carouselItems(carouselItems);
    }

    public ag.act.model.StockHomeSectionResponse convert(ConvertStockHomeSectionResponseDto dto) {
        return new StockHomeSectionResponse()
            .type("list")
            .header(
                new ListSectionHeaderResponse()
                    .title(dto.getHeaderTitle())
                    .link(dto.getAppLinkUrl())
            )
            .listItems(dto.getListItems());
    }
}
