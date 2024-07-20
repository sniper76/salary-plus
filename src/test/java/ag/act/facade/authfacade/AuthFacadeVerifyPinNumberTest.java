package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.converter.user.UserConverter;
import ag.act.entity.User;
import ag.act.enums.ActErrorCode;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.InternalServerException;
import ag.act.exception.VerificationPinNumberException;
import ag.act.facade.auth.AuthFacade;
import ag.act.model.UserResponse;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFacadeVerifyPinNumberTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserService userService;
    @Mock
    private UserConverter userConverter;
    @Mock
    private CryptoHelper cryptoHelper;
    @Mock
    private UserResponse userResponse;
    @Mock
    private UserVerificationHistoryService userVerificationHistoryService;
    @Mock
    private User savedUser;
    @Mock
    private User user;
    @Mock
    private ag.act.model.PinNumberRequest pinNumberRequest;
    private String pinNumber;
    private LocalDateTime localDateTime;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(LocalDateTime.class));

        pinNumber = someNumericString(6);
        localDateTime = LocalDateTime.now();
        final String hashedPinNumber = someString(5);
        userId = someLong();

        given(user.getId()).willReturn(userId);
        given(user.getHashedPinNumber()).willReturn(hashedPinNumber);
        given(pinNumberRequest.getPinNumber()).willReturn(pinNumber);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(LocalDateTime.now()).willReturn(localDateTime);
        given(cryptoHelper.encrypt(pinNumber)).willReturn(hashedPinNumber);
        given(userService.saveUser(user)).willReturn(savedUser);
        given(userConverter.convert(savedUser)).willReturn(userResponse);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class Success {

        private UserResponse result;

        @BeforeEach
        void setUp() {
            result = facade.verifyPinNumber(pinNumberRequest);
        }

        @Test
        void shouldReturnTheUserResponse() {
            assertThat(result, is(userResponse));
        }

        @Test
        void shouldEncryptPinNumber() throws Exception {
            then(cryptoHelper).should().encrypt(pinNumber);
        }

        @Test
        void shouldChangeUserInfoByRequest() {
            then(user).should().setLastPinNumberVerifiedAt(localDateTime);
        }

        @Test
        void shouldSaveUser() {
            then(userService).should().saveUser(user);
        }

        @Test
        void shouldConvertSavedUser() {
            then(userConverter).should().convert(savedUser);
        }

        @Test
        void shouldCreateVerificationHistory() {
            then(userVerificationHistoryService).should().create(userId, VerificationType.PIN, VerificationOperationType.VERIFICATION);
        }
    }

    @Nested
    class WhenFailedToVerifyPinNumber {

        private VerificationPinNumberException exception;

        @BeforeEach
        void setUp() {

            // Given
            given(user.getHashedPinNumber()).willReturn(someString(20));

            // When
            exception = assertThrows(
                VerificationPinNumberException.class,
                () -> facade.verifyPinNumber(pinNumberRequest)
            );
        }

        @Test
        void shouldThrowExceptionWithCorrectMessage() {
            assertThat(exception.getMessage(), is("비밀번호가 일치하지 않습니다\n다시 입력해주세요"));
        }

        @Test
        void shouldThrowExceptionWithCorrectErrorCode() {
            assertThat(exception.getErrorCode(), is(ActErrorCode.PIN_VERIFICATION_FAILED.getCode()));
        }

        @Test
        void shouldNotSaveUser() {
            then(userService).should(never()).saveUser(user);
        }

        @Test
        void shouldNotConvertUserResponse() {
            then(userConverter).should(never()).convert(savedUser);
        }
    }

    @Nested
    class WhenFailedToEncryptPinNumber {

        private InternalServerException exception;

        @BeforeEach
        void setUp() throws Exception {

            // Given
            given(cryptoHelper.encrypt(pinNumber)).willThrow(new RuntimeException());

            // When
            exception = assertThrows(
                InternalServerException.class,
                () -> facade.verifyPinNumber(pinNumberRequest)
            );
        }

        @Test
        void shouldThrowExceptionWithCorrectMessage() {
            assertThat(exception.getMessage(), is("핀번호를 검증하는 중에 알 수 없는 오류가 발생하였습니다."));
        }

        @Test
        void shouldNotSaveUser() {
            then(userService).should(never()).saveUser(user);
        }

        @Test
        void shouldNotConvertUserResponse() {
            then(userConverter).should(never()).convert(savedUser);
        }
    }
}
