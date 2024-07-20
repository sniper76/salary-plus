package ag.act.facade.popup;

import ag.act.converter.popup.PopupResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.popup.PopupSearchDto;
import ag.act.entity.Popup;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.exception.NotFoundException;
import ag.act.model.PopupDetailsResponse;
import ag.act.model.PopupRequest;
import ag.act.service.PopupService;
import ag.act.validator.PopupValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PopupFacade {
    private final PopupService popupService;
    private final PopupResponseConverter popupResponseConverter;
    private final PopupValidator popupValidator;
    private final PopupDetailsResponseEnricher popupDetailsResponseEnricher;

    public SimplePageDto<PopupDetailsResponse> getPopupListItems(
        PopupSearchDto popupSearchDto,
        Pageable pageable
    ) {
        final Page<Popup> popupPage = popupService.getPopupList(popupSearchDto, pageable);
        return new SimplePageDto<>(popupPage.map(popupResponseConverter));
    }

    public PopupDetailsResponse getPopupDetails(Long popupId) {
        final Popup popup = getPopupNonNull(popupId);
        return enrichPopupDetailsWithStockInfo(popup, popupResponseConverter.convert(popup));
    }

    public PopupDetailsResponse createPopup(PopupRequest popupRequest) {
        popupValidator.validate(popupRequest);

        final Popup popup = popupService.createPopup(popupRequest);
        return enrichPopupDetailsWithStockInfo(popup, popupResponseConverter.convert(popup));
    }

    public PopupDetailsResponse updatePopup(PopupRequest popupRequest) {
        popupValidator.validateUpdate(popupRequest);

        final Popup updatedPopup = popupService.updatePopup(popupRequest, getPopupNonNull(popupRequest.getId()));
        return enrichPopupDetailsWithStockInfo(updatedPopup, popupResponseConverter.convert(updatedPopup));
    }

    public PopupDetailsResponse getExclusivePopup(String displayTargetType, String stockCode) {
        final Popup popup = popupService.getExclusivePopup(PopupDisplayTargetType.fromValue(displayTargetType), stockCode);
        return enrichPopupDetailsWithStockInfo(popup, popupResponseConverter.convert(popup));
    }

    public ag.act.model.SimpleStringResponse deletePopup(Long popupId) {
        return popupService.deletePopup(popupId);
    }

    private Popup getPopupNonNull(Long popupId) {
        return popupService.getPopup(popupId)
            .orElseThrow(() -> new NotFoundException("해당 팝업을 찾을 수 없습니다."));
    }

    private PopupDetailsResponse enrichPopupDetailsWithStockInfo(Popup popup, PopupDetailsResponse popupDetailsResponse) {
        return popupDetailsResponseEnricher.enrichStockInfo(popup, popupDetailsResponse);
    }
}
