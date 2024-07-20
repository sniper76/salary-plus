package ag.act.handler.admin;

import ag.act.api.AdminAuthenticationApiDelegate;
import ag.act.configuration.security.ActUserProvider;
import ag.act.core.guard.IsAdminOrAcceptorUserRoleGuard;
import ag.act.core.guard.NoGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.auth.AuthFacade;
import ag.act.facade.user.UserFacade;
import ag.act.model.CmsLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminAuthenticationApiDelegateImpl implements AdminAuthenticationApiDelegate {

    private final AuthFacade authFacade;
    private final UserFacade userFacade;

    @Override
    @UseGuards(NoGuard.class)
    public ResponseEntity<ag.act.model.AuthUserResponse> loginAdmin(CmsLoginRequest cmsLoginRequest) {
        return ResponseEntity.ok(authFacade.loginInCms(cmsLoginRequest));
    }

    @Override
    @UseGuards(IsAdminOrAcceptorUserRoleGuard.class)
    public ResponseEntity<ag.act.model.UserDataResponse> changeMyPassword(ag.act.model.ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(userFacade.changePassword(ActUserProvider.getNoneNull().getId(), changePasswordRequest));
    }
}
