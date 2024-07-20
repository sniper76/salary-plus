package ag.act.converter.home;

import ag.act.entity.Post;
import ag.act.enums.BoardGroup;
import ag.act.model.CarouselItemResponse;
import ag.act.model.CarouselSectionHeaderResponse;
import ag.act.model.SectionItemResponse;
import ag.act.util.AppLinkUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CarouselItemConverter {
    private final SectionItemResponseConverter sectionItemResponseConverter;
    private final CarouselHeaderImageConverter carouselHeaderImageConverter;
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    public CarouselItemResponse convert(String stockCode, BoardGroup boardGroup, List<Post> posts) {
        List<SectionItemResponse> listItems = posts.stream()
            .map(sectionItemResponseConverter::convert)
            .toList();

        return new CarouselItemResponse()
            .header(convertToSectionHeader(stockCode, boardGroup))
            .listItems(listItems);
    }

    private CarouselSectionHeaderResponse convertToSectionHeader(String stockCode, BoardGroup boardGroup) {
        return new CarouselSectionHeaderResponse()
            .title(boardGroup.getDisplayName())
            .link(appLinkUrlGenerator.generateBoardGroupLinkUrl(stockCode, boardGroup))
            .image(carouselHeaderImageConverter.convert(boardGroup));
    }
}
