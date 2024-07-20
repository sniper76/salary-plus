package ag.act.service;

import ag.act.converter.RequestValueConverter;
import ag.act.converter.user.SimpleUserProfileDtoForDetailsConverter;
import ag.act.core.infra.S3Environment;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import ag.act.repository.UserRepository;
import ag.act.service.io.FileContentService;
import ag.act.service.user.UserBadgeVisibilityService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.ImageUtil;
import ag.act.validator.user.NicknameValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someEmail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @InjectMocks
    private UserService service;
    private List<MockedStatic<?>> statics;
    @Mock
    private S3Environment s3Environment;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileContentService fileContentService;
    @Mock
    private NicknameHistoryService nicknameHistoryService;
    @Mock
    private UserBadgeVisibilityService userBadgeVisibilityService;
    @Mock
    private RequestValueConverter requestValueConverter;
    @Mock
    private SimpleUserProfileDtoForDetailsConverter simpleUserProfileDtoForDetailsConverter;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private NicknameValidator nicknameValidator;
    @Mock
    private UserVerificationHistoryService userVerificationHistoryService;
    @Mock
    private User user;
    private User actualUser;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ImageUtil.class));

        given(userRepository.saveAndFlush(user)).willReturn(user);
        given(requestValueConverter.convertBoolean(any())).willAnswer(invocation -> Boolean.parseBoolean(invocation.getArgument(0)));
        given(requestValueConverter.convert(any())).willAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class InvalidatePinNumberVerificationTime {

        @BeforeEach
        void setUp() {
            actualUser = service.invalidatePinNumberVerificationTime(user);
        }

        @Test
        void shouldReturnInvalidatedUser() {
            assertThat(actualUser, is(user));
        }

        @Test
        void shouldSetNullToUpdateLastPinNumberVerifiedAt() {
            then(user).should().setLastPinNumberVerifiedAt(null);
        }

        @Test
        void shouldSaveUser() {
            then(userRepository).should().saveAndFlush(user);
        }
    }

    @Nested
    class ResetPinNumber {
        @BeforeEach
        void setUp() {
            actualUser = service.resetPinNumber(user);
        }

        @Test
        void shouldReturnResetUser() {
            assertThat(actualUser, is(user));
        }

        @Test
        void shouldSetNullToHashedPinNumber() {
            then(user).should().setHashedPinNumber(null);
        }

        @Test
        void shouldSetNullToLastPinNumberVerifiedAt() {
            then(user).should().setLastPinNumberVerifiedAt(null);
        }

        @Test
        void shouldSaveUser() {
            then(userRepository).should().saveAndFlush(user);
        }
    }

    @Nested
    class UpdateMyProfile {
        @Mock
        private ag.act.model.UpdateMyProfileRequest request;
        @Mock
        private User user;
        private User actualUser;
        private String jonTitle;
        private String mySpeech;
        private String address;
        private String addressDetail;
        private String zipcode;
        private Boolean isAgreeToReceiveMail;


        @BeforeEach
        void setUp() {
            given(userRepository.saveAndFlush(user)).willReturn(user);
        }

        @Nested
        class WhenAllFieldsAreEmpty extends UpdateMyProfileDefaultTestCases {

            @BeforeEach
            void setUp() {
                jonTitle = "";
                mySpeech = "";
                address = "";
                addressDetail = "";
                zipcode = "";
                isAgreeToReceiveMail = Boolean.FALSE;

                given(requestValueConverter.convert(any())).willReturn("");
                given(requestValueConverter.convertBoolean(any())).willReturn(false);


                actualUser = service.updateMyProfile(user, request);
            }
        }

        @Nested
        class WhenAllFieldsHaveSomeValues extends UpdateMyProfileDefaultTestCases {

            @BeforeEach
            void setUp() {
                jonTitle = someAlphanumericString(5);
                mySpeech = someAlphanumericString(7);
                address = someAlphanumericString(9);
                addressDetail = someAlphanumericString(11);
                zipcode = someAlphanumericString(13);
                isAgreeToReceiveMail = someBoolean();

                given(request.getJobTitle()).willReturn(jonTitle + " ");
                given(request.getMySpeech()).willReturn(mySpeech + " ");
                given(request.getAddress()).willReturn(address + " ");
                given(request.getAddressDetail()).willReturn(addressDetail + " ");
                given(request.getZipcode()).willReturn(zipcode + " ");
                given(request.getIsAgreeToReceiveMail()).willReturn(isAgreeToReceiveMail);
                given(requestValueConverter.convert(any()))
                    .willAnswer(invocation -> ((String) invocation.getArgument(0)).trim());
                given(requestValueConverter.convertBoolean(any()))
                    .willAnswer(invocation -> Boolean.parseBoolean(((String) invocation.getArgument(0)).trim()));

                actualUser = service.updateMyProfile(user, request);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class UpdateMyProfileDefaultTestCases {

            @Test
            void shouldReturnTheUpdatedUser() {
                assertThat(actualUser, is(user));
            }


            @Test
            void shouldUpdateWithJobTitle() {
                then(user).should().setJobTitle(jonTitle);
            }

            @Test
            void shouldUpdateWithMySpeech() {
                then(user).should().setMySpeech(mySpeech);
            }

            @Test
            void shouldUpdateWithAddress() {
                then(user).should().setAddress(address);
            }

            @Test
            void shouldUpdateWithAddressDetail() {
                then(user).should().setAddressDetail(addressDetail);
            }

            @Test
            void shouldUpdateWithZipcode() {
                then(user).should().setZipcode(zipcode);
            }

            @Test
            void shouldUpdateWithIsAgreeToReceiveMail() {
                then(user).should().setIsAgreeToReceiveMail(isAgreeToReceiveMail);
            }

            @Test
            void shouldCallSaveUser() {
                then(userRepository).should().saveAndFlush(user);
            }
        }
    }

    @Nested
    class UpdateMyAddress {
        @Mock
        private ag.act.model.UpdateMyAddressRequest request;
        @Mock
        private User user;
        private User actualUser;
        private String address;
        private String addressDetail;
        private String zipcode;

        @BeforeEach
        void setUp() {
            given(userRepository.saveAndFlush(user)).willReturn(user);
        }

        @Nested
        class WhenAllFieldsAreEmpty extends UpdateMyAddressDefaultTestCases {

            @BeforeEach
            void setUp() {
                address = "";
                addressDetail = "";
                zipcode = "";

                given(requestValueConverter.convert(any())).willReturn("");

                actualUser = service.updateMyAddress(user, request);
            }
        }

        @Nested
        class WhenAllFieldsHaveSomeValues extends UpdateMyAddressDefaultTestCases {

            @BeforeEach
            void setUp() {
                address = someAlphanumericString(9);
                addressDetail = someAlphanumericString(11);
                zipcode = someAlphanumericString(13);

                given(request.getAddress()).willReturn(address + " ");
                given(request.getAddressDetail()).willReturn(addressDetail + " ");
                given(request.getZipcode()).willReturn(zipcode + " ");
                given(requestValueConverter.convert(any()))
                    .willAnswer(invocation -> ((String) invocation.getArgument(0)).trim());
                actualUser = service.updateMyAddress(user, request);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class UpdateMyAddressDefaultTestCases {

            @Test
            void shouldReturnTheUpdatedUser() {
                assertThat(actualUser, is(user));
            }

            @Test
            void shouldUpdateWithAddress() {
                then(user).should().setAddress(address);
            }

            @Test
            void shouldUpdateWithAddressDetail() {
                then(user).should().setAddressDetail(addressDetail);
            }

            @Test
            void shouldUpdateWithZipcode() {
                then(user).should().setZipcode(zipcode);
            }

            @Test
            void shouldCallSaveUser() {
                then(userRepository).should().saveAndFlush(user);
            }
        }
    }

    @Nested
    class RegisterUserInfo {
        @Mock
        private ag.act.model.RegisterUserInfoRequest request;
        private String email;
        private String nickname;
        private Boolean isAgreeToReceiveMail;
        private ag.act.model.Gender gender;

        @BeforeEach
        void setUp() {
            email = someEmail();
            nickname = someAlphanumericString(10);
            isAgreeToReceiveMail = someBoolean();
            gender = someEnum(ag.act.model.Gender.class);

            given(request.getEmail()).willReturn(email);
            given(request.getNickname()).willReturn(nickname);
            given(request.getIsAgreeToReceiveMail()).willReturn(isAgreeToReceiveMail);
            given(user.getGender()).willReturn(gender);
        }

        @Nested
        class WhenUserAlreadyHasProfileImage extends RegisterUserInfoDefaultTestCases {
            @BeforeEach
            void setUp() {

                given(user.getProfileImageUrl()).willReturn(someAlphanumericString(10));

                actualUser = service.registerUserInfo(user, request);
            }

            @Test
            void shouldCallNicknameValidator() {
                then(nicknameValidator).should().validateNickname(user);
                then(nicknameValidator).should().validateNicknameWithin90Days(user);
            }


            @Test
            void shouldNotCallUploadProfileImage() {
                then(fileContentService).shouldHaveNoInteractions();
            }
        }

        @Nested
        class WhenUserDoesNotHasProfileImage extends RegisterUserInfoDefaultTestCases {

            @Mock
            private FileContent fileContent;
            private String profileImageUrl;

            @BeforeEach
            void setUp() {
                final String baseUrl = someString(10);
                final String fileName = someString(15);
                profileImageUrl = someAlphanumericString(20);

                given(fileContentService.getPickOneDefaultProfileImage(gender)).willReturn(fileContent);
                given(s3Environment.getBaseUrl()).willReturn(baseUrl);
                given(fileContent.getFilename()).willReturn(fileName);
                given(ImageUtil.getFullPath(baseUrl, fileName)).willReturn(profileImageUrl);

                actualUser = service.registerUserInfo(user, request);
            }

            @Test
            void shouldPickOneDefaultProfileImage() {
                then(fileContentService).should().getPickOneDefaultProfileImage(gender);
            }

            @Test
            void shouldUpdateTheProfileImageUrl() {
                then(user).should().setProfileImageUrl(profileImageUrl);
            }

        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class RegisterUserInfoDefaultTestCases {

            @Test
            void shouldReturnTheUpdatedUser() {
                assertThat(actualUser, is(user));
            }

            @Test
            void shouldCallSaveUser() {
                then(userRepository).should().saveAndFlush(user);
            }

            @Test
            void shouldUpdateWithNickname() {
                then(user).should().setNickname(nickname);
            }

            @Test
            void shouldUpdateWithIsAgreeToReceiveMail() {
                then(user).should().setIsAgreeToReceiveMail(isAgreeToReceiveMail);
            }

            @Test
            void shouldUpdateNickname() {
                then(user).should().setNickname(nickname);
            }

            @Test
            void shouldUpdateEmail() {
                then(user).should().setEmail(email);
            }

            @Test
            void shouldUpdateStatusToActive() {
                then(user).should().setStatus(ag.act.model.Status.ACTIVE);
            }

            @Test
            void shouldCreateNicknameHistory() {
                then(nicknameHistoryService).should().create(user);
            }
        }
    }

    @Nested
    class GetActiveUserHoldingStocksMap {

        private long userId;
        @Mock
        private User user;

        @BeforeEach
        void setUp() {
            userId = someLong();
        }

        @Nested
        class WhenUserExists {

            @Mock
            private UserHoldingStock userHoldingStock1Active;
            @Mock
            private UserHoldingStock userHoldingStock2Inactive;
            private String stockCode;
            private Map<String, UserHoldingStock> actualActiveUserHoldingStocksMap;

            @BeforeEach
            void setUp() {
                stockCode = someString(6);
                final List<UserHoldingStock> userHoldingStocks = List.of(userHoldingStock1Active, userHoldingStock2Inactive);

                given(userHoldingStock1Active.getStatus()).willReturn(Status.ACTIVE);
                given(userHoldingStock2Inactive.getStatus()).willReturn(Status.INACTIVE_BY_USER);
                given(userHoldingStock1Active.getStockCode()).willReturn(stockCode);
                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(user.getUserHoldingStocks()).willReturn(userHoldingStocks);

                actualActiveUserHoldingStocksMap = service.getActiveUserHoldingStocksMap(userId);
            }

            @Test
            void shouldReturnOnlyOneRecordInMap() {
                assertThat(actualActiveUserHoldingStocksMap.size(), is(1));
            }

            @Test
            void shouldReturnTheActiveUserHoldingStocksMap() {
                assertThat(actualActiveUserHoldingStocksMap, is(Map.of(stockCode, userHoldingStock1Active)));
            }

            @Test
            void shouldCallUserRepositoryFindById() {
                then(userRepository).should().findById(userId);
            }
        }

        @Nested
        class WhenUserNotFound {

            @BeforeEach
            void setUp() {
                given(userRepository.findById(userId)).willReturn(Optional.empty());
            }

            @Test
            void shouldThrowNotFoundException() {
                // When
                final NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> service.getActiveUserHoldingStocksMap(userId)
                );

                assertThat(exception.getMessage(), is("회원을 찾을 수 없습니다."));
            }
        }
    }

    @Nested
    class ValidateUniqueEmail {

        private Long userId;
        private String email;
        private String trimmedEmail;

        @BeforeEach
        void setUp() {
            userId = someLong();
            email = " " + someString(5) + " ";
            trimmedEmail = email.trim();

            given(userRepository.findUserByEmail(trimmedEmail)).willReturn(Optional.empty());
        }

        @Nested
        class WhenCanNotFoundTheSameEmailInDatabase {
            @BeforeEach
            void setUp() {
                service.validateUniqueEmail(userId, email);
            }

            @Test
            void shouldCallUserRepositoryToFindUserByEmail() {
                then(userRepository).should().findUserByEmail(trimmedEmail);
            }
        }

        @Nested
        class WhenFindTheSameEmailInDatabase {

            @Mock
            private User existingUser;

            @Nested
            class AndSameUserIsUsingThatEmail {

                @Test
                void shouldCallUserRepositoryToFindUserByEmail() {

                    // Given
                    given(existingUser.getId()).willReturn(userId);
                    given(userRepository.findUserByEmail(trimmedEmail)).willReturn(Optional.of(existingUser));

                    // When
                    service.validateUniqueEmail(userId, email);

                    // Then
                    then(userRepository).should().findUserByEmail(trimmedEmail);
                }
            }

            @Nested
            class AndDifferentUserIsUsingTheSameEmail {

                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(existingUser.getId()).willReturn(someLong());
                    given(userRepository.findUserByEmail(trimmedEmail)).willReturn(Optional.of(existingUser));

                    // When
                    final BadRequestException exception = assertThrows(
                        BadRequestException.class,
                        () -> service.validateUniqueEmail(userId, email)
                    );

                    // Then
                    assertThat(exception.getMessage(), is("이미 사용중인 이메일입니다."));
                }
            }

        }
    }

    @Nested
    class WithdrawUser {

        @Mock
        private User user;

        @BeforeEach
        void setUp() {
            service.withdrawRequest(user);
        }

        @Test
        void shouldUpdateUserWithDeletedStatus() {
            then(user).should().setStatus(Status.WITHDRAWAL_REQUESTED);
        }

        @Test
        void shouldSetNowToDeletedAt() {
            then(user).should().setDeletedAt(any(LocalDateTime.class));
        }

        @Test
        void shouldSetNullToLastPinNumberVerifiedAt() {
            then(user).should().setLastPinNumberVerifiedAt(null);
        }

        @Test
        void shouldSetNullToHashedPinNumber() {
            then(user).should().setHashedPinNumber(null);
        }

        @Test
        void shouldCallSaveUser() {
            then(userRepository).should().saveAndFlush(user);
        }
    }

    @Nested
    class GetUserProfileResponse {

        @Mock
        private UserHoldingStock userHoldingStock1;
        @Mock
        private UserHoldingStock userHoldingStock2;
        @Mock
        private SimpleUserProfileDto simpleUserProfileDto;

        private String stockCode;

        @Nested
        class AndSuccess {

            @BeforeEach
            void setUp() {
                stockCode = someString(5);
                final List<UserHoldingStock> userHoldingStocks = List.of(userHoldingStock1, userHoldingStock2);

                given(user.getUserHoldingStocks()).willReturn(userHoldingStocks);
                given(userHoldingStock1.getStockCode()).willReturn(someString(5));
                given(userHoldingStock2.getStockCode()).willReturn(stockCode);
                given(userHoldingStockService.findQueriedUserHoldingStock(stockCode, userHoldingStocks))
                    .willReturn(userHoldingStock2);
                given(simpleUserProfileDtoForDetailsConverter.convert(user, userHoldingStock2, userHoldingStocks))
                    .willReturn(simpleUserProfileDto);
            }

            @Test
            void shouldReturnUserSimpleProfile() {

                // When
                final SimpleUserProfileDto actual = service.getSimpleUserProfileDto(user, stockCode);

                // Then
                assertThat(actual, Matchers.is(simpleUserProfileDto));
            }
        }
    }
}
