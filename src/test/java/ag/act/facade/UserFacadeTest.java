package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.image.ImageResponseConverter;
import ag.act.converter.user.UserConverter;
import ag.act.converter.user.UserDataResponseConverter;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.FileContent;
import ag.act.entity.NicknameHistory;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.NotFoundException;
import ag.act.facade.user.UserFacade;
import ag.act.model.AuthType;
import ag.act.model.CreateBlockUserRequest;
import ag.act.model.SimpleImageDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.model.UserDataResponse;
import ag.act.service.NicknameHistoryService;
import ag.act.service.io.FileContentService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserWithdrawalProcessService;
import ag.act.service.user.UserWithdrawalRequestService;
import ag.act.util.badge.StockCountLabelGenerator;
import ag.act.util.badge.TotalAssetLabelGenerator;
import ag.act.validator.DefaultObjectValidator;
import ag.act.validator.user.NicknameValidator;
import ag.act.validator.user.PasswordValidator;
import ag.act.validator.user.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("unused")
@MockitoSettings(strictness = Strictness.LENIENT)
class UserFacadeTest {

    @InjectMocks
    private UserFacade facade;
    @Mock
    private UserService userService;
    @Mock
    private UserWithdrawalProcessService userWithdrawalProcessService;
    @Mock
    private UserWithdrawalRequestService userWithdrawalRequestService;
    @Mock
    private FileContentService fileContentService;
    @Mock
    private UserDataResponseConverter userDataResponseConverter;
    @Mock
    private DefaultObjectValidator defaultObjectValidator;
    @Mock
    private TotalAssetLabelGenerator totalAssetLabelGenerator;
    @Mock
    private StockCountLabelGenerator stockCountLabelGenerator;
    @Mock
    private ImageResponseConverter imageResponseConverter;
    @Mock
    private NicknameHistoryService nicknameHistoryService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private PasswordValidator passwordValidator;
    @Mock
    private UserPasswordService userPasswordService;
    @SuppressWarnings("unused")
    @Mock
    private UserConverter userConverter;
    @Mock
    private NicknameValidator nicknameValidator;
    @Mock
    private BlockedUserService blockedUserService;

    private List<MockedStatic<?>> statics;
    @Mock
    private User user;
    private Long userId;
    private String stockCode;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        userId = someLong();
        stockCode = someStockCode();

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
        given(userService.findUser(userId)).willReturn(Optional.of(user));
        given(defaultObjectValidator.validateAndGet(Optional.of(user), "회원정보를 찾을 수 없습니다.")).willReturn(user);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class GetUserProfileResponse {

        @Nested
        class AndUserNotFound {

            @BeforeEach
            void setUp() {
                given(userService.findUser(userId)).willReturn(Optional.empty());
                given(defaultObjectValidator.validateAndGet(any(), anyString())).willThrow(NotFoundException.class);
            }

            @Test
            void shouldReturnUserSimpleProfile() {

                // When // then
                assertThrows(
                    NotFoundException.class,
                    () -> facade.getSimpleUserProfileDtoWithStockValidation(userId, stockCode)
                );
            }
        }
    }

    @Nested
    class GetUserSimpleProfile {

        @Mock
        private UserHoldingStock userHoldingStock1;
        @Mock
        private UserHoldingStock userHoldingStock2;
        @Mock
        private SimpleUserProfileDto userProfileResponse;

        @Nested
        class AndUserHasSomeUserHoldingStocks {

            @BeforeEach
            void setUp() {
                final String stockCountLabel = someString(5);
                final String totalAssetLabel = someString(5);
                final List<UserHoldingStock> userHoldingStocks = List.of(userHoldingStock1, userHoldingStock2);

                given(userService.findUser(userId)).willReturn(Optional.of(user));
                given(defaultObjectValidator.validateAndGet(any(), anyString())).willReturn(user);
                given(user.getUserHoldingStocks()).willReturn(userHoldingStocks);
                given(userHoldingStock1.getStockCode()).willReturn(someString(5));
                given(userHoldingStock2.getStockCode()).willReturn(stockCode);
                given(stockCountLabelGenerator.generate(userHoldingStock2)).willReturn(stockCountLabel);
                given(totalAssetLabelGenerator.generate(userHoldingStocks)).willReturn(totalAssetLabel);
                given(userService.getSimpleUserProfileDto(user, stockCode)).willReturn(userProfileResponse);
            }

            @Nested
            class AndSuccess {

                @Test
                void shouldReturnUserSimpleProfile() {

                    // When
                    final SimpleUserProfileDto actual = facade.getSimpleUserProfileDtoWithStockValidation(userId, stockCode);

                    // Then
                    assertThat(actual, is(userProfileResponse));
                    then(userValidator).should().validateStatus(user);
                    then(userValidator).should().validateHavingStock(user, stockCode);
                }
            }

            @Nested
            class AndStockCodeIsBlank {
                @Test
                void shouldReturnUserSimpleProfile() {

                    // Given
                    stockCode = "         ";
                    given(userService.getSimpleUserProfileDto(user, stockCode)).willReturn(userProfileResponse);
                    given(stockCountLabelGenerator.generate(null)).willReturn(someString(5));

                    // When
                    final SimpleUserProfileDto actual = facade.getSimpleUserProfileDtoWithStockValidation(userId, stockCode);

                    // Then
                    assertThat(actual, is(userProfileResponse));
                    then(userValidator).should().validateStatus(user);
                    then(userValidator).should(never()).validateHavingStock(user, stockCode);
                }
            }
        }
    }

    @Nested
    class UpdateMyPushToken {

        private String token;
        private SimpleStringResponse actualResult;

        @BeforeEach
        void setUp() {
            token = someString(5);

            given(userService.saveUser(user)).willReturn(user);

            actualResult = facade.updateMyPushToken(token);
        }

        @Test
        void shouldSaveUser() {
            then(userService).should().saveUser(user);
        }

        @Test
        void shouldReturnSimpleOkayResponse() {
            assertThat(actualResult.getStatus(), is("ok"));
        }

        @Test
        void shouldSetPushToken() {
            then(user).should().setPushToken(token);
        }
    }

    @Nested
    class UpdateMyProfileImage {

        @Mock
        private FileContent fileContent;
        @Mock
        private SimpleImageDataResponse simpleImageDataResponse;
        @Mock
        private ag.act.model.SimpleImageResponse simpleImageResponse;
        private SimpleImageDataResponse actualResponse;

        @BeforeEach
        void setUp() {
            final Long imageId = someLong();
            final String imageUrl = someString(5);

            final Optional<FileContent> fileContentOptional = Optional.of(fileContent);
            given(fileContentService.findById(imageId)).willReturn(fileContentOptional);
            given(defaultObjectValidator.validateAndGet(fileContentOptional, "이미지를 찾을 수 없습니다.")).willReturn(fileContent);
            given(imageResponseConverter.convertImageUrl(fileContent)).willReturn(imageUrl);
            given(userService.saveUser(user)).willReturn(user);
            given(imageResponseConverter.convertToSimpleImageResponse(fileContent)).willReturn(simpleImageResponse);
            given(imageResponseConverter.convert(simpleImageResponse)).willReturn(simpleImageDataResponse);

            actualResponse = facade.updateMyProfileImage(imageId);
        }

        @Test
        void shouldUpdateMyProfileImage() {
            assertThat(actualResponse, is(simpleImageDataResponse));
        }

        @Test
        void shouldSetProfileImageUrl() {
            then(user).should().setProfileImageUrl(anyString());
        }
    }

    @Nested
    class UpdateMyProfile {
        @Mock
        private ag.act.model.UpdateMyProfileRequest request;
        @Mock
        private ag.act.model.UserDataResponse userDataResponse;
        private UserDataResponse actual;

        @BeforeEach
        void setUp() {
            given(userService.updateMyProfile(user, request)).willReturn(user);
            given(userDataResponseConverter.convert(user)).willReturn(userDataResponse);

            actual = facade.updateMyProfile(request);
        }

        @Test
        void shouldReturnUserDataResponse() {
            assertThat(actual, is(userDataResponse));
        }

        @Test
        void shouldUpdateProfileThroughUserService() {
            then(userService).should().updateMyProfile(user, request);
        }
    }

    @Nested
    class UpdateMyAddress {
        @Mock
        private ag.act.model.UpdateMyAddressRequest request;
        @Mock
        private ag.act.model.UserDataResponse userDataResponse;
        private UserDataResponse actual;

        @BeforeEach
        void setUp() {
            given(userService.updateMyAddress(user, request)).willReturn(user);
            given(userDataResponseConverter.convert(user)).willReturn(userDataResponse);

            actual = facade.updateMyAddress(request);
        }

        @Test
        void shouldReturnUserDataResponse() {
            assertThat(actual, is(userDataResponse));
        }

        @Test
        void shouldUpdateAddressThroughUserService() {
            then(userService).should().updateMyAddress(user, request);
        }
    }

    @Nested
    class UpdateMyNickname {
        @Mock
        private NicknameHistory nicknameHistory;
        private SimpleStringResponse actual;

        @BeforeEach
        void setUp() {
            final String nickname = someString(10);

            given(nicknameHistoryService.create(user)).willReturn(nicknameHistory);
            given(userService.saveUser(user)).willReturn(user);

            actual = facade.updateMyNickname(nickname);
        }

        @Test
        void shouldReturnOkay() {
            assertThat(actual.getStatus(), is("ok"));
        }

        @Test
        void shouldCallNicknameValidator() {
            then(nicknameValidator).should().validateNickname(user);
            then(nicknameValidator).should().validateNicknameWithin90Days(user);
        }

        @Test
        void shouldCallNicknameHistoryServiceCreate() {
            then(nicknameHistoryService).should().create(user);
        }

        @Test
        void shouldCallUserServiceSaveUser() {
            then(userService).should().saveUser(user);
        }
    }

    @Nested
    class WhenUpdateAuthType {
        private SimpleStringResponse actual;

        @BeforeEach
        void setUp() {

            given(defaultObjectValidator.validateAndGet(any(), anyString())).willReturn(user);
            given(userService.saveUser(user)).willReturn(user);

            actual = facade.updateUserAuthType(AuthType.BIO.toString());
        }

        @Test
        void shouldReturnOkay() {
            assertThat(actual.getStatus(), is("ok"));
        }

        @Test
        void shouldCallUserServiceSaveUser() {
            then(userService).should().saveUser(user);
        }
    }

    @Nested
    class WithdrawUser {

        private Status deletedByUserStatus;

        @BeforeEach
        void setUp() {
            deletedByUserStatus = Status.DELETED_BY_USER;

            facade.withdrawUser(user, deletedByUserStatus);
        }

        @Test
        void shouldCallUserServiceWithdrawUser() {
            then(userWithdrawalProcessService).should().withdrawUser(user, deletedByUserStatus);
        }
    }

    @Nested
    class ChangePassword {

        @Mock
        private ag.act.model.ChangePasswordRequest request;
        private UserDataResponse actualResponse;
        @Mock
        private UserDataResponse expectedResponse;
        private String newPassword;

        @BeforeEach
        void setUp() {

            final String userEncryptedPassword = someString(10);
            newPassword = someString(20);

            given(user.getPassword()).willReturn(userEncryptedPassword);
            given(userService.saveUser(user)).willReturn(user);
            given(userDataResponseConverter.convert(user)).willReturn(expectedResponse);
            willDoNothing().given(passwordValidator).validateChangePassword(request, userEncryptedPassword);
            given(request.getPassword()).willReturn(newPassword);

            actualResponse = facade.changePassword(userId, request);
        }

        @Test
        void shouldReturnUserDataResponse() {
            assertThat(actualResponse, is(expectedResponse));
        }

        @Test
        void shouldSetPasswordAndChangeRequired() {
            then(userPasswordService).should().setPasswordAndChangeRequired(user, newPassword.trim(), Boolean.FALSE);
        }
    }

    @Nested
    class BlockUser {

        @Mock
        private CreateBlockUserRequest request;
        @Mock
        private User targetUser;

        @BeforeEach
        void setUp() {
            final Long targetUserId = someLong();
            given(request.getTargetUserId()).willReturn(targetUserId);
            given(userService.findUser(targetUserId)).willReturn(Optional.of(targetUser));
            given(defaultObjectValidator.validateAndGet(Optional.of(targetUser), "회원정보를 찾을 수 없습니다.")).willReturn(targetUser);

            facade.blockUser(request);
        }

        @Test
        void shouldCallBlockUser() {
            then(blockedUserService).should().blockUser(user, targetUser);
        }

    }

    @Nested
    class UnblockUser {

        @Mock
        private User blockedUser;
        private Long blockedUserId;

        @BeforeEach
        void setUp() {
            blockedUserId = someLong();
            given(userService.findUser(blockedUserId)).willReturn(Optional.of(blockedUser));
            given(defaultObjectValidator.validateAndGet(Optional.of(blockedUser), "회원정보를 찾을 수 없습니다.")).willReturn(blockedUser);
            given(blockedUser.getId()).willReturn(blockedUserId);

            facade.unBlockUser(blockedUserId);
        }

        @Test
        void shouldCallUnBlockUser() {
            then(blockedUserService).should().unblockUser(userId, blockedUserId);
        }
    }
}
