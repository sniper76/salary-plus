package ag.act.facade.user;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.image.ImageResponseConverter;
import ag.act.converter.user.UserConverter;
import ag.act.converter.user.UserDataResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.download.DownloadFile;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.dto.user.UserSearchFilterDto;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.AuthType;
import ag.act.model.ChangePasswordRequest;
import ag.act.model.CreateBlockUserRequest;
import ag.act.model.CreateSolidarityLeaderConfidentialAgreementRequest;
import ag.act.model.GetSolidarityLeaderConfidentialAgreementResponse;
import ag.act.model.SimpleImageDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateMyAddressRequest;
import ag.act.model.UpdateMyProfileRequest;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import ag.act.module.user.documentdownload.UserDocumentDownloadService;
import ag.act.service.NicknameHistoryService;
import ag.act.service.confidential.SolidarityLeaderConfidentialAgreementService;
import ag.act.service.io.FileContentService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserWithdrawalProcessService;
import ag.act.service.user.UserWithdrawalRequestService;
import ag.act.util.DateTimeUtil;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.validator.DefaultObjectValidator;
import ag.act.validator.user.NicknameValidator;
import ag.act.validator.user.PasswordValidator;
import ag.act.validator.user.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserFacade {

    private static final int ONE_DAY = 1;
    private final UserService userService;
    private final UserDocumentDownloadService userDocumentDownloadService;
    private final UserWithdrawalProcessService userWithdrawalProcessService;
    private final UserWithdrawalRequestService userWithdrawalRequestService;
    private final FileContentService fileContentService;
    private final NicknameHistoryService nicknameHistoryService;
    private final DefaultObjectValidator defaultObjectValidator;
    private final UserDataResponseConverter userDataResponseConverter;
    private final ImageResponseConverter imageResponseConverter;
    private final UserValidator userValidator;
    private final UserConverter userConverter;
    private final PasswordValidator passwordValidator;
    private final UserPasswordService userPasswordService;
    private final NicknameValidator nicknameValidator;
    private final BlockedUserService blockedUserService;
    private final SolidarityLeaderConfidentialAgreementService solidarityLeaderConfidentialAgreementService;

    public UserDataResponse getUserDataResponse(Long userId) {
        final User user = userService.findUser(userId)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        return userDataResponseConverter.convert(user);
    }

    public SimpleUserProfileDto getSimpleUserProfileDto(Long userId, String stockCode) {
        final User user = validateUserAndGet(userId);
        return userService.getSimpleUserProfileDto(user, stockCode);
    }

    public SimpleUserProfileDto getSimpleUserProfileDtoWithStockValidation(Long userId, String stockCode) {
        final User user = validateUserAndGet(userId);
        userValidator.validateStatus(user);
        if (StringUtils.isNotBlank(stockCode)) {
            userValidator.validateHavingStock(user, stockCode);
        }
        return userService.getSimpleUserProfileDto(user, stockCode);
    }

    public SimpleImageDataResponse updateMyProfileImage(Long imageId) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());
        final FileContent fileContent
            = defaultObjectValidator.validateAndGet(fileContentService.findById(imageId), "이미지를 찾을 수 없습니다.");

        user.setProfileImageUrl(imageResponseConverter.convertImageUrl(fileContent));
        userService.saveUser(user);

        return imageResponseConverter.convert(imageResponseConverter.convertToSimpleImageResponse(fileContent));
    }

    public UserDataResponse updateMyProfile(UpdateMyProfileRequest updateMyProfileRequest) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());

        return userDataResponseConverter.convert(userService.updateMyProfile(user, updateMyProfileRequest));
    }

    public UserDataResponse updateMyAddress(UpdateMyAddressRequest updateMyAddressRequest) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());

        return userDataResponseConverter.convert(userService.updateMyAddress(user, updateMyAddressRequest));
    }

    public SimpleStringResponse updateMyPushToken(String token) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());
        user.setPushToken(token);
        userService.saveUser(user);

        return SimpleStringResponseUtil.ok();
    }

    public SimpleStringResponse updateMyNickname(String nickname) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());
        user.setNickname(validateUniqueNicknameAndGet(nickname, user.getId()));

        nicknameValidator.validateNickname(user);
        nicknameValidator.validateNicknameWithin90Days(user);
        nicknameValidator.validateIfUserAppliedForSolidarityLeaderElection(user);
        nicknameHistoryService.create(user);
        userService.saveUser(user);

        return SimpleStringResponseUtil.ok();
    }

    public UserDataResponse updateUserNickname(Long userId, String nickname) {
        final User user = validateUserAndGet(userId);
        user.setNickname(validateUniqueNicknameAndGet(nickname, user.getId()));

        nicknameValidator.validateNickname(user);
        nicknameHistoryService.createByAdmin(user);

        return userDataResponseConverter.convert(userService.saveUser(user));
    }

    private String validateUniqueNicknameAndGet(String nickname, Long userId) {
        final String trimmedNickname = nickname.trim();
        userService.validateUniqueNickname(userId, trimmedNickname);
        return trimmedNickname;
    }

    public SimpleStringResponse updateUserAuthType(String authType) {
        final User user = validateUserAndGet(ActUserProvider.getNoneNull().getId());
        user.setAuthType(AuthType.valueOf(authType));

        userService.saveUser(user);
        return SimpleStringResponseUtil.ok();
    }

    public SimpleStringResponse requestWithdrawal() {
        userWithdrawalRequestService.requestWithdrawal(ActUserProvider.getNoneNull().getId());

        return SimpleStringResponseUtil.ok();
    }

    public boolean withdrawUser(User user, ag.act.model.Status status) {
        try {
            userWithdrawalProcessService.withdrawUser(user, status);
        } catch (Exception e) {
            log.error("Failed to withdraw user", e);
            return false;
        }
        return true;
    }

    public List<User> getWithdrawRequestedUsers() {
        return userService.getWithdrawRequestedUsers();
    }

    public List<User> getWithdrawRequestedUsersBeforeOneDay() {
        return getWithdrawRequestedUsers().stream()
            .filter(this::isOneDayPast)
            .toList();
    }

    private boolean isOneDayPast(User user) {
        return DateTimeUtil.isBeforeInDays(user.getUpdatedAt(), ONE_DAY);
    }

    public User validateUserAndGet(Long userId) {
        return defaultObjectValidator.validateAndGet(userService.findUser(userId), "회원정보를 찾을 수 없습니다.");
    }

    public SimplePageDto<UserResponse> getUsers(UserSearchFilterDto userSearchFilterDto, PageRequest pageRequest) {
        final Page<User> userPage = userService.getUserList(userSearchFilterDto, pageRequest);
        return new SimplePageDto<>(userPage.map(userConverter));
    }

    public UserDataResponse changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        final User user = validateUserAndGet(userId);
        passwordValidator.validateChangePassword(changePasswordRequest, user.getPassword());
        userPasswordService.setPasswordAndChangeRequired(user, changePasswordRequest.getPassword().trim(), Boolean.FALSE);

        return userDataResponseConverter.convert(userService.saveUser(user));
    }

    public void blockUser(CreateBlockUserRequest createBlockUserRequest) {
        final User user = ActUserProvider.getNoneNull();
        final User targetUser = validateUserAndGet(createBlockUserRequest.getTargetUserId());

        blockedUserService.blockUser(user, targetUser);
    }

    public void unBlockUser(Long blockedUserId) {
        final User user = ActUserProvider.getNoneNull();
        final User blockedUser = validateUserAndGet(blockedUserId);
        blockedUserService.unblockUser(user.getId(), blockedUser.getId());
    }

    public DownloadFile getSolidarityLeaderConfidentialAgreementDocument(Long userId) {
        final User user = userService.getUser(userId);

        return userDocumentDownloadService.downloadSolidarityLeaderConfidentialAgreementDocument(user);
    }

    public GetSolidarityLeaderConfidentialAgreementResponse getSolidarityLeaderConfidentialAgreementDocumentForm() {
        final User user = ActUserProvider.getNoneNull();
        return new GetSolidarityLeaderConfidentialAgreementResponse()
            .content(solidarityLeaderConfidentialAgreementService.getSolidarityLeaderConfidentialAgreementDocumentForm(user));
    }

    public SimpleStringResponse createSolidarityLeaderConfidentialAgreementDocument(
        CreateSolidarityLeaderConfidentialAgreementRequest createSolidarityLeaderConfidentialAgreementRequest
    ) {
        final User user = ActUserProvider.getNoneNull();
        final Map<String, UserHoldingStock> userHoldingStockMap = userService.getAllUserHoldingStocksMapIncludingTestStocks(user.getId());
        if (!userHoldingStockMap.containsKey(createSolidarityLeaderConfidentialAgreementRequest.getStockCode())) {
            throw new BadRequestException("보유하지 않은 종목에 비밀유지 서약서를 제출할 수 없습니다.");
        }
        solidarityLeaderConfidentialAgreementService.createSolidarityLeaderConfidentialAgreementDocument(user);
        return SimpleStringResponseUtil.ok();
    }
}
