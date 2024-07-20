package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.configuration.security.TokenProvider;
import ag.act.converter.kcb.KcbOkCertRequestConverter;
import ag.act.converter.kcb.KcbOkCertResponseConverter;
import ag.act.converter.user.AuthUserResponseConverter;
import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.DeletedUserException;
import ag.act.exception.InternalServerException;
import ag.act.facade.auth.SmsAuthFacade;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import ag.act.service.KcbService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.validator.user.UserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class SmsAuthFacadeTest {

    private List<MockedStatic<?>> statics;
    @InjectMocks
    private SmsAuthFacade facade;
    @Mock
    private KcbService kcbService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private UserService userService;
    @Mock
    private UserVerificationHistoryService userVerificationHistoryService;
    @Mock
    private CryptoHelper cryptoHelper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUserResponseConverter authUserResponseConverter;
    @Mock
    private KcbOkCertRequestConverter kcbOkCertRequestConverter;
    @Mock
    private KcbOkCertResponseConverter kcbOkCertResponseConverter;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private User user;
    @Mock
    private UserVerificationHistory userVerificationHistory;

    private String body;
    private String responseBody;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        // Given
        body = someString(5);
        responseBody = someString(10);
        Optional<User> userOptional = Optional.of(user);
        given(ActUserProvider.get()).willReturn(userOptional);
        willDoNothing().given(userValidator).validateStatus(userOptional);
        given(userVerificationHistoryService.create(anyLong(), eq(VerificationType.SMS), eq(VerificationOperationType.REGISTER)))
            .willReturn(userVerificationHistory);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class SendAuthRequest {

        @Mock
        private ag.act.model.SendAuthRequest sendAuthRequest;
        @Mock
        private ag.act.model.SendAuthResponse sendAuthResponse;
        @Mock
        private OkCertSendRequest okCertSendRequest;
        @Mock
        private OkCertSendResponse okCertSendResponse;

        @BeforeEach
        void setUp() throws JsonProcessingException {

            given(objectMapper.writeValueAsString(okCertSendRequest)).willReturn(body);
            given(objectMapper.readValue(responseBody, OkCertSendResponse.class))
                .willReturn(okCertSendResponse);
            given(kcbService.sendAuthRequest(okCertSendRequest)).willReturn(okCertSendResponse);
            given(kcbOkCertRequestConverter.convertSendAuthRequest(sendAuthRequest))
                .willReturn(okCertSendRequest);
            given(kcbOkCertResponseConverter.convertSendAuthResponse(okCertSendResponse))
                .willReturn(sendAuthResponse);
        }

        @Test
        void shouldSendMobileAuthRequest() {

            // When
            final ag.act.model.SendAuthResponse actual = facade.sendAuthRequest(sendAuthRequest);

            // Then
            assertThat(actual, is(sendAuthResponse));
            then(kcbOkCertRequestConverter).should().convertSendAuthRequest(sendAuthRequest);
            then(kcbOkCertResponseConverter).should().convertSendAuthResponse(okCertSendResponse);
        }

        @Nested
        class AndRequestParsingErrorOccurred {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                given(kcbOkCertRequestConverter.convertSendAuthRequest(sendAuthRequest))
                    .willThrow(RuntimeException.class);

                // When // Then
                assertException(
                    InternalServerException.class,
                    () -> facade.sendAuthRequest(sendAuthRequest),
                    "인증서버의 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요."
                );
            }
        }

        @Nested
        class AndResponseParsingErrorOccurred {

            @Test
            void shouldThrowInternalServerException() {
                // Given
                given(kcbOkCertResponseConverter.convertSendAuthResponse(okCertSendResponse))
                    .willThrow(RuntimeException.class);

                // When // Then
                assertException(
                    InternalServerException.class,
                    () -> facade.sendAuthRequest(sendAuthRequest),
                    "인증서버의 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요."
                );
            }
        }

    }

    @Nested
    class VerifyAuthCode {

        @Mock
        private ag.act.model.VerifyAuthCodeRequest verifyAuthCodeRequest;
        @Mock
        private ag.act.model.AuthUserResponse authUserResponse;
        @Mock
        private OkCertVerifyRequest okCertVerifyRequest;
        @Mock
        private OkCertVerifyResponse responseDto;
        @Mock
        private User user;
        private Long userId;
        private String hashedCI;
        private String accessToken;
        private String di;

        @BeforeEach
        void setUp() throws Exception {

            final String ci = someString(5);
            final String phoneNumber = someString(7);
            final String hashedPhoneNumber = someString(17);
            hashedCI = someString(10);
            accessToken = someString(30);
            userId = 1L;

            given(kcbOkCertRequestConverter.convertVerifyAuthRequest(verifyAuthCodeRequest))
                .willReturn(okCertVerifyRequest);
            given(kcbService.verifyAuthCode(okCertVerifyRequest))
                .willReturn(responseDto);
            given(verifyAuthCodeRequest.getPhoneNumber()).willReturn(phoneNumber);
            given(responseDto.getCi()).willReturn(ci);
            given(cryptoHelper.encrypt(ci)).willReturn(hashedCI);
            given(cryptoHelper.encrypt(phoneNumber)).willReturn(hashedPhoneNumber);
            given(userService.getUserByHashedCI(hashedCI)).willReturn(Optional.of(user));
            given(user.getId()).willReturn(userId);

            given(authUserResponseConverter.convert(user, accessToken, null))
                .willReturn(authUserResponse);
        }

        @Nested
        class AndUserExists {

            @BeforeEach
            void setUp() {
                given(tokenProvider.createAppToken(userId.toString())).willReturn(accessToken);
            }

            @Test
            void shouldVerifyAuthCodeAndNotCreateNewUser() {

                // When
                final ag.act.model.AuthUserResponse actual = facade.verifyAuthCode(verifyAuthCodeRequest);

                // Then
                assertThat(actual, is(authUserResponse));
                then(kcbService).should().verifyAuthCode(okCertVerifyRequest);
                then(passwordEncoder).should(never()).encode(di);
                then(userService).should().getUserByHashedCI(hashedCI);
                then(tokenProvider).should().createAppToken(userId.toString());
                then(authUserResponseConverter).should().convert(user, accessToken, null);
                then(userVerificationHistoryService).should().create(userId, VerificationType.SMS, VerificationOperationType.REGISTER);
            }
        }

        @Nested
        class AndUserNotExists {

            @Mock
            private User savedUser;
            @Captor
            private ArgumentCaptor<User> userArgumentCaptor;
            private String trimmedName;

            @BeforeEach
            void setUp() {
                di = someString(15);
                final String hashedDI = someString(20);
                final String firstName = someAlphanumericString(10);
                final String lastName = someAlphanumericString(10);
                trimmedName = firstName + lastName;
                final String name = " " + firstName + "   " + lastName + "   ";

                given(responseDto.getDi()).willReturn(di);
                given(passwordEncoder.encode(di)).willReturn(hashedDI);
                given(userService.getUserByHashedCI(hashedCI)).willReturn(Optional.empty());
                given(userService.saveUser(any(User.class))).willReturn(savedUser);

                given(verifyAuthCodeRequest.getBirthDate()).willReturn("19770530");
                given(verifyAuthCodeRequest.getGender()).willReturn(someEnum(ag.act.model.Gender.class).toString());
                given(verifyAuthCodeRequest.getName()).willReturn(name);
                given(savedUser.getId()).willReturn(userId);
                given(tokenProvider.createAppToken(userId.toString())).willReturn(accessToken);
                given(authUserResponseConverter.convert(savedUser, accessToken, null))
                    .willReturn(authUserResponse);
            }

            @Test
            void shouldVerifyAuthCodeAndShouldCreateNewUser() {

                // When
                final ag.act.model.AuthUserResponse actual = facade.verifyAuthCode(verifyAuthCodeRequest);

                // Then
                assertThat(actual, is(authUserResponse));
                then(kcbService).should().verifyAuthCode(okCertVerifyRequest);
                then(passwordEncoder).should().encode(di);
                then(userService).should().getUserByHashedCI(hashedCI);
                then(userService).should().saveUser(userArgumentCaptor.capture());
                final User user = userArgumentCaptor.getValue();
                assertThat(user.getName(), is(trimmedName));
                then(tokenProvider).should().createAppToken(userId.toString());
                then(authUserResponseConverter).should().convert(savedUser, accessToken, null);
                then(userVerificationHistoryService).should().create(userId, VerificationType.SMS, VerificationOperationType.REGISTER);
            }

        }

        @Nested
        class AndUserIsDeletedOrWithdrawalRequested {

            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
            @Mock
            private Optional<User> optionalUserByHashedCI;

            @BeforeEach
            void setUp() {
                given(tokenProvider.createAppToken(userId.toString())).willReturn(accessToken);
                given(userService.getUserByHashedCI(hashedCI)).willReturn(optionalUserByHashedCI);
                willThrow(DeletedUserException.class).given(userValidator).validateStatus(optionalUserByHashedCI);
            }

            @Test
            void shouldNotSaveUser() {

                // When
                assertThrows(
                    DeletedUserException.class,
                    () -> facade.verifyAuthCode(verifyAuthCodeRequest)
                );

                // Then
                then(kcbService).should().verifyAuthCode(okCertVerifyRequest);
                then(userService).should().getUserByHashedCI(hashedCI);
                then(passwordEncoder).should(never()).encode(di);
                then(tokenProvider).should(never()).createAppToken(userId.toString());
                then(authUserResponseConverter).should(never()).convert(user, accessToken, null);
                then(userVerificationHistoryService).should(never()).create(userId, VerificationType.SMS, VerificationOperationType.REGISTER);
            }
        }
    }

    @Nested
    class ResendAuthRequest {

        @Mock
        private ag.act.model.ResendAuthRequest resendAuthRequest;
        @Mock
        private OkCertResendRequest requestDto;
        @Mock
        private OkCertResendResponse responseDto;
        @Mock
        private ag.act.model.ResendAuthResponse resendAuthResponse;

        @BeforeEach
        void setUp() {
            given(kcbOkCertRequestConverter.convertResendAuthRequest(resendAuthRequest)).willReturn(requestDto);
            given(kcbService.resendAuthRequest(requestDto)).willReturn(responseDto);
            given(kcbOkCertResponseConverter.convertResendAuthResponse(responseDto)).willReturn(resendAuthResponse);
        }

        @Test
        void shouldResentAuthRequest() {

            // When
            final ag.act.model.ResendAuthResponse actual = facade.resendAuthRequest(resendAuthRequest);

            // Then
            assertThat(actual, is(resendAuthResponse));
            then(kcbService).should().resendAuthRequest(requestDto);
            then(kcbOkCertResponseConverter).should().convertResendAuthResponse(responseDto);
        }
    }
}
