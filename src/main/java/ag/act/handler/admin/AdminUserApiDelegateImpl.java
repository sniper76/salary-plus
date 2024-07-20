package ag.act.handler.admin;

import ag.act.api.AdminUserApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.dto.user.UserSearchFilterDto;
import ag.act.exception.BadRequestException;
import ag.act.facade.auth.AuthFacade;
import ag.act.facade.user.UserFacade;
import ag.act.model.GetUserResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminUserApiDelegateImpl implements AdminUserApiDelegate {
    private final UserFacade userFacade;
    private final AuthFacade authFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<UserDataResponse> getUserDetailsAdmin(Long userId) {
        return ResponseEntity.ok(userFacade.getUserDataResponse(userId));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> withdrawMyDataWithToken(String finpongAccessToken) {
        if (StringUtils.isBlank(finpongAccessToken)) {
            throw new BadRequestException("마이데이터 토큰을 확인해주세요.");
        }
        authFacade.withdrawMyDataService(finpongAccessToken);
        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<GetUserResponse> getUsers(
        String filterType,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<UserResponse> userListItems = userFacade.getUsers(
            new UserSearchFilterDto(filterType, searchType, searchKeyword),
            pageDataConverter.convert(page, size, sorts));
        return ResponseEntity.ok(pageDataConverter.convert(userListItems, GetUserResponse.class));
    }

    @Override
    public ResponseEntity<UserDataResponse> editUserNickname(Long userId, ag.act.model.EditUserNicknameRequest editUserNicknameRequest) {
        return ResponseEntity.ok(userFacade.updateUserNickname(userId, editUserNicknameRequest.getNickname()));
    }
}
