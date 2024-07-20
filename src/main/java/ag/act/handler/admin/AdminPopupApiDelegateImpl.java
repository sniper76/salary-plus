package ag.act.handler.admin;

import ag.act.api.AdminPopupApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.dto.popup.PopupSearchDto;
import ag.act.facade.popup.PopupFacade;
import ag.act.model.GetPopupDataResponse;
import ag.act.model.PopupDataResponse;
import ag.act.model.PopupDetailsDataResponse;
import ag.act.model.PopupDetailsResponse;
import ag.act.model.PopupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminPopupApiDelegateImpl implements AdminPopupApiDelegate {
    private final PopupFacade popupFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetPopupDataResponse> getPopupsAdmin(
        String popupStatus,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<PopupDetailsResponse> popupListItems = popupFacade.getPopupListItems(
            new PopupSearchDto(popupStatus, searchType, searchKeyword),
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(popupListItems, GetPopupDataResponse.class));
    }

    @Override
    public ResponseEntity<PopupDetailsDataResponse> getPopupDetailsAdmin(Long popupId) {
        return ResponseEntity.ok(
            new ag.act.model.PopupDetailsDataResponse().data(popupFacade.getPopupDetails(popupId))
        );
    }

    @Override
    public ResponseEntity<PopupDataResponse> createPopup(PopupRequest popupRequest) {
        return ResponseEntity.ok(new PopupDataResponse().data(popupFacade.createPopup(popupRequest)));
    }

    @Override
    public ResponseEntity<PopupDataResponse> updatePopup(Long popupId, PopupRequest popupRequest) {
        popupRequest.setId(popupId);
        return ResponseEntity.ok(new PopupDataResponse().data(popupFacade.updatePopup(popupRequest)));
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> deletePopup(Long popupId) {
        return ResponseEntity.ok(popupFacade.deletePopup(popupId));
    }
}
