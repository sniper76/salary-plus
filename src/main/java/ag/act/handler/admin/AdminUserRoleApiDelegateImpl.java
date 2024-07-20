package ag.act.handler.admin;

import ag.act.api.AdminUserRoleApiDelegate;
import ag.act.core.guard.IsSuperAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.user.UserRoleFacade;
import ag.act.model.AddRoleToUserRequest;
import ag.act.model.UserDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@UseGuards({IsSuperAdminGuard.class})
@RequiredArgsConstructor
@Service
public class AdminUserRoleApiDelegateImpl implements AdminUserRoleApiDelegate {
    private final UserRoleFacade userRoleFacade;

    @Override
    public ResponseEntity<UserDataResponse> addRoleToUser(Long userId, AddRoleToUserRequest addRoleToUserRequest) {
        return ResponseEntity.ok(userRoleFacade.addRoleToUser(userId, addRoleToUserRequest));
    }

    @Override
    public ResponseEntity<UserDataResponse> assignAdminToUser(Long userId) {
        return ResponseEntity.ok(userRoleFacade.assignAdminRoleToUser(userId));
    }

    @Override
    public ResponseEntity<UserDataResponse> revokeAdminToUser(Long userId) {
        return ResponseEntity.ok(userRoleFacade.revokeAdminToUser(userId));
    }
}
