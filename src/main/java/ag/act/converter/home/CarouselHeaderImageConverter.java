package ag.act.converter.home;

import ag.act.enums.BoardGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CarouselHeaderImageConverter {
    private final String analysisHeaderImageUrl;
    private final String debateHeaderImageUrl;
    private final String defaultHeaderImageUrl;

    public CarouselHeaderImageConverter(
        @Value("${boardGroup.stock-home-header-image.analysis.url}") final String analysisHeaderImageUrl,
        @Value("${boardGroup.stock-home-header-image.debate.url}") final String debateHeaderImageUrl,
        @Value("${boardGroup.stock-home-header-image.default.url}") final String defaultHeaderImageUrl
    ) {
        this.analysisHeaderImageUrl = analysisHeaderImageUrl;
        this.debateHeaderImageUrl = debateHeaderImageUrl;
        this.defaultHeaderImageUrl = defaultHeaderImageUrl;
    }

    public String convert(BoardGroup boardGroup) {
        return switch (boardGroup) {
            case ANALYSIS -> analysisHeaderImageUrl;
            case DEBATE -> debateHeaderImageUrl;
            default -> defaultHeaderImageUrl;
        };
    }
}
