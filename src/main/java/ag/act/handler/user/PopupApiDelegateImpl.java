package ag.act.handler.user;

import ag.act.api.PopupApiDelegate;
import ag.act.facade.popup.PopupFacade;
import ag.act.model.PopupDetailsDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopupApiDelegateImpl implements PopupApiDelegate {
    private final PopupFacade popupFacade;

    @Override
    public ResponseEntity<PopupDetailsDataResponse> getExclusivePopup(String displayTargetType, String stockCode) {
        return ResponseEntity.ok(
            new PopupDetailsDataResponse().data(popupFacade.getExclusivePopup(displayTargetType, stockCode))
        );
    }
}
