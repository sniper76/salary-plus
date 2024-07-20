package ag.act.service.stock.home;

import ag.act.model.StockHomeSectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StockHomeSectionService {
    private final StockHomeListSectionService stockHomeListSectionService;
    private final StockHomeCarouselSectionService stockHomeCarouselSectionService;

    public List<StockHomeSectionResponse> getSections(String stockCode) {
        final StockHomeSectionResponse carouselSection = stockHomeCarouselSectionService.getSections(stockCode);
        final List<StockHomeSectionResponse> listSections = stockHomeListSectionService.getSections(stockCode);

        return Stream.concat(Stream.of(carouselSection), listSections.stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
