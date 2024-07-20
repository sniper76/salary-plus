package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.converter.user.UserConverter;
import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.InternalServerException;
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
class AuthFacadeRegisterPinNumberTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserService userService;
    @Mock
    private UserVerificationHistoryService userVerificationHistoryService;
    @Mock
    private UserConverter userConverter;
    @Mock
    private CryptoHelper cryptoHelper;
    @Mock
    private UserResponse userResponse;
    @Mock
    private User savedUser;
    @Mock
    private User user;
    @Mock
    private UserVerificationHistory userVerificationHistory;
    @Mock
    private ag.act.model.PinNumberRequest pinNumberRequest;
    private String pinNumber;
    private String hashedPinNumber;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(LocalDateTime.class));

        pinNumber = someNumericString(6);
        hashedPinNumber = someString(5);
        final Long userId = someLong();
        localDateTime = LocalDateTime.now();

        given(user.getId()).willReturn(userId);
        given(pinNumberRequest.getPinNumber()).willReturn(pinNumber);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(LocalDateTime.now()).willReturn(localDateTime);
        given(cryptoHelper.encrypt(pinNumber)).willReturn(hashedPinNumber);
        given(userService.saveUser(user)).willReturn(savedUser);
        given(userConverter.convert(savedUser)).willReturn(userResponse);
        given(userVerificationHistoryService.create(userId, VerificationType.PIN, VerificationOperationType.REGISTER))
            .willReturn(userVerificationHistory);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class RegisterPinNumberSuccess {

        private UserResponse result;

        @BeforeEach
        void setUp() {
            result = facade.registerPinNumber(pinNumberRequest);
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
            then(user).should().setHashedPinNumber(hashedPinNumber);
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
        void shouldCreateUserVerificationHistory() {
            then(userVerificationHistoryService).should().create(user.getId(), VerificationType.PIN, VerificationOperationType.REGISTER);
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
                () -> facade.registerPinNumber(pinNumberRequest)
            );
        }

        @Test
        void shouldThrowExceptionWithCorrectMessage() {
            assertThat(exception.getMessage(), is("핀번호를 업데이트하는 중에 알 수 없는 오류가 발생하였습니다."));
        }

        @Test
        void shouldNotSaveUser() {
            then(userService).should(never()).saveUser(user);
        }

        @Test
        void shouldNotConvertUserResponse() {
            then(userConverter).should(never()).convert(savedUser);
        }

        @Test
        void shouldNotCreateUserVerificationHistory() {
            then(userVerificationHistoryService).should(never()).create(user.getId(), VerificationType.PIN, VerificationOperationType.REGISTER);
        }
    }
}
