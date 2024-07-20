package ag.act.handler;

import ag.act.api.DigitalProxyApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.digitaldocument.DigitalProxyModuSignFacade;
import ag.act.model.DigitalProxySignResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class DigitalProxyApiDelegateImpl implements DigitalProxyApiDelegate {

    private final DigitalProxyModuSignFacade digitalProxyModuSignFacade;

    public DigitalProxyApiDelegateImpl(DigitalProxyModuSignFacade digitalProxyModuSignFacade) {
        this.digitalProxyModuSignFacade = digitalProxyModuSignFacade;
    }

    @Override
    public ResponseEntity<DigitalProxySignResponse> signAndGetEmbeddedUrl(String stockCode, String boardGroupName, Long postId) {
        return ResponseEntity.ok(digitalProxyModuSignFacade.signAndGetEmbeddedUrl(stockCode, boardGroupName, postId));
    }
}
