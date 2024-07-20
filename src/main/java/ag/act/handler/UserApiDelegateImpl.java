package ag.act.handler;

import ag.act.api.UserApiDelegate;
import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.converter.user.UserDataResponseConverter;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.enums.admin.BlockedUserFilterType;
import ag.act.facade.user.MyDataFacade;
import ag.act.facade.user.MyStockAuthenticationFacade;
import ag.act.facade.user.UserFacade;
import ag.act.model.BlockedUserResponse;
import ag.act.model.CreateBlockUserRequest;
import ag.act.model.CreateSolidarityLeaderConfidentialAgreementRequest;
import ag.act.model.GetAnonymousCountResponse;
import ag.act.model.GetBlockedUserResponse;
import ag.act.model.GetSolidarityLeaderConfidentialAgreementResponse;
import ag.act.model.MyStockAuthenticationResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SimpleUserProfileDataResponse;
import ag.act.model.UpdateMyAddressRequest;
import ag.act.model.UpdateMyAuthTypeRequest;
import ag.act.model.UpdateMyDataRequest;
import ag.act.model.UpdateMyNicknameRequest;
import ag.act.model.UpdateMyProfileImageRequest;
import ag.act.model.UpdateMyProfileRequest;
import ag.act.model.UpdateMyPushTokenRequest;
import ag.act.model.UserDataResponse;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.user.UserAnonymousCountService;
import ag.act.util.DateTimeUtil;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.validator.DefaultRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@UseGuards({IsActiveUserGuard.class})
@RequiredArgsConstructor
public class UserApiDelegateImpl implements UserApiDelegate {
    private final MyStockAuthenticationFacade myStockAuthenticationFacade;
    private final UserFacade userFacade;
    private final MyDataFacade myDataFacade;
    private final UserDataResponseConverter userDataResponseConverter;
    private final UserAnonymousCountService userAnonymousCountService;
    private final BlockedUserService blockedUserService;
    private final DefaultRequestValidator defaultRequestValidator;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetAnonymousCountResponse> getUserAnonymousCount() {
        final String currentDate = DateTimeUtil.getFormattedDateTimeByZone("yyyyMMdd", "UTC+9", Instant.now());
        return ResponseEntity.ok(userAnonymousCountService.getUserAnonymousCount(currentDate));
    }

    @Override
    public ResponseEntity<UserDataResponse> getMe() {
        return getCurrentUserResponse();
    }

    @Override
    public ResponseEntity<SimpleUserProfileDataResponse> getUserSimpleProfile(Long userId, String stockCode) {
        return ResponseEntity.ok(
            new SimpleUserProfileDataResponse().data(
                userFacade.getSimpleUserProfileDtoWithStockValidation(userId, stockCode).toResponse()
            )
        );
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleImageDataResponse> updateMyProfileImage(UpdateMyProfileImageRequest updateMyProfileImageRequest) {
        defaultRequestValidator.validateNotNull(updateMyProfileImageRequest.getImageId(), "프로파일 이미지 정보를 확인해주세요.");
        return ResponseEntity.ok(userFacade.updateMyProfileImage(updateMyProfileImageRequest.getImageId()));
    }

    @Override
    public ResponseEntity<UserDataResponse> updateMyProfile(UpdateMyProfileRequest updateMyProfileRequest) {
        return ResponseEntity.ok(userFacade.updateMyProfile(updateMyProfileRequest));
    }

    @Override
    public ResponseEntity<UserDataResponse> updateMyAddress(UpdateMyAddressRequest updateMyAddressRequest) {
        return ResponseEntity.ok(userFacade.updateMyAddress(updateMyAddressRequest));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateMyPushToken(UpdateMyPushTokenRequest updateMyPushTokenRequest) {
        defaultRequestValidator.validateNotNull(updateMyPushTokenRequest.getToken(), "푸시토큰을 확인해주세요.");
        return ResponseEntity.ok(userFacade.updateMyPushToken(updateMyPushTokenRequest.getToken()));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateMyNickname(UpdateMyNicknameRequest updateMyNicknameRequest) {
        defaultRequestValidator.validateNotNull(updateMyNicknameRequest.getNickname(), "닉네임을 확인해주세요.");
        return ResponseEntity.ok(userFacade.updateMyNickname(updateMyNicknameRequest.getNickname()));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateMyAuthType(UpdateMyAuthTypeRequest updateMyAuthTypeRequest) {
        return ResponseEntity.ok(userFacade.updateUserAuthType(updateMyAuthTypeRequest.getAuthType()));
    }

    @Override
    public ResponseEntity<UserDataResponse> updateMyData(UpdateMyDataRequest updateMyDataRequest) {
        myDataFacade.updateMyData(updateMyDataRequest);

        return getCurrentUserResponse();
    }

    @Override
    public ResponseEntity<SimpleStringResponse> blockUser(CreateBlockUserRequest createBlockUserRequest) {
        userFacade.blockUser(createBlockUserRequest);
        return ResponseEntity.ok(SimpleStringResponseUtil.ok());
    }

    @Override
    public ResponseEntity<SimpleStringResponse> unblockUser(Long blockedUserId) {
        userFacade.unBlockUser(blockedUserId);
        return ResponseEntity.ok(SimpleStringResponseUtil.ok());
    }

    @Override
    public ResponseEntity<SimpleStringResponse> requestWithdrawal() {
        return ResponseEntity.ok(userFacade.requestWithdrawal());
    }

    @NotNull
    private ResponseEntity<UserDataResponse> getCurrentUserResponse() {
        return ResponseEntity.ok(userDataResponseConverter.convert(ActUserProvider.getNoneNull()));
    }

    @Override
    public ResponseEntity<GetBlockedUserResponse> getBlockedUsers(
        String blockedUserType,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<BlockedUserResponse> blockedUsers = blockedUserService.getBlockedUsers(
            BlockedUserFilterType.fromValue(blockedUserType),
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(blockedUsers, GetBlockedUserResponse.class));
    }

    @Override
    public ResponseEntity<GetSolidarityLeaderConfidentialAgreementResponse> getSolidarityLeaderConfidentialAgreementDocumentForm() {
        return ResponseEntity.ok(userFacade.getSolidarityLeaderConfidentialAgreementDocumentForm());
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityLeaderConfidentialAgreementDocument(
        CreateSolidarityLeaderConfidentialAgreementRequest createSolidarityLeaderConfidentialAgreementRequest
    ) {
        return ResponseEntity.ok(userFacade.createSolidarityLeaderConfidentialAgreementDocument(
            createSolidarityLeaderConfidentialAgreementRequest
        ));
    }

    @Override
    public ResponseEntity<MyStockAuthenticationResponse> getMyStockAuthentication(String stockCode) {
        return ResponseEntity.ok(
            myStockAuthenticationFacade.getMyStockAuthentication(stockCode)
        );
    }
}
