package ag.act.dto.popup;

import ag.act.entity.Popup;
import ag.act.enums.popup.PopupSearchType;
import ag.act.enums.popup.PopupStatus;
import ag.act.specification.PopupSpecification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class PopupSearchDto {
    private PopupStatus popupStatus;
    private PopupSearchType popupSearchType;
    private String keyword;

    public PopupSearchDto(
        String popupStatus, String searchTypeName, String keyword
    ) {
        this.popupStatus = PopupStatus.fromValue(popupStatus);
        this.popupSearchType = PopupSearchType.fromValue(searchTypeName);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return Optional.ofNullable(keyword).map(String::trim).orElse(null);
    }

    public boolean isKeywordBlank() {
        return StringUtils.isBlank(keyword);
    }

    public Specification<Popup> getTargetDatetimeSpecification() {
        return switch (popupStatus) {
            case READY -> PopupSpecification.isFuture();
            case PROCESSING -> PopupSpecification.isInProgress();
            case COMPLETE -> PopupSpecification.isPast();
            case ALL -> PopupSpecification.empty();
        };
    }

    public Specification<Popup> getTitleContainsSpecification() {
        return PopupSpecification.titleContains(keyword);
    }
}
