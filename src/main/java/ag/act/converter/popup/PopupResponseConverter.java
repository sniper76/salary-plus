package ag.act.converter.popup;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Popup;
import ag.act.enums.popup.PopupStatus;
import ag.act.model.PopupDetailsResponse;
import org.springframework.stereotype.Component;

@Component
public class PopupResponseConverter implements Converter<Popup, PopupDetailsResponse> {
    public PopupDetailsResponse convert(Popup popup) {
        if (popup == null) {
            return null;
        }
        return new PopupDetailsResponse()
            .id(popup.getId())
            .title(popup.getTitle())
            .content(popup.getContent())
            .linkType(popup.getLinkType().name())
            .linkTitle(popup.getLinkTitle())
            .linkUrl(popup.getLinkUrl())
            .displayTargetType(popup.getDisplayTargetType().name())
            .stockCode(popup.getStockCode())
            .stockGroupId(popup.getStockGroupId())
            .stockTargetType(popup.getStockTargetType().name())
            .targetStartDatetime(DateTimeConverter.convert(popup.getTargetStartDatetime()))
            .targetEndDatetime(DateTimeConverter.convert(popup.getTargetEndDatetime()))
            .popupStatus(PopupStatus.fromTargetDatetime(popup.getTargetStartDatetime(), popup.getTargetEndDatetime()))
            .createdAt(DateTimeConverter.convert(popup.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(popup.getUpdatedAt()))
            .postId(popup.getPostId())
            ;
    }

    @Override
    public PopupDetailsResponse apply(Popup popup) {
        return convert(popup);
    }
}
