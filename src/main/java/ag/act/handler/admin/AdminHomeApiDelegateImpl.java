package ag.act.handler.admin;

import ag.act.api.AdminHomeApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.HomeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminHomeApiDelegateImpl implements AdminHomeApiDelegate {
    private final HomeFacade homeFacade;

    @Override
    public ResponseEntity<ag.act.model.HomeLinkResponse> updateHomeLink(
        String linkType,
        ag.act.model.UpdateHomeLinkRequest updateHomeLinkRequest
    ) {
        return ResponseEntity.ok(homeFacade.updateHomeLink(linkType, updateHomeLinkRequest.getLinkUrl()));
    }
}
