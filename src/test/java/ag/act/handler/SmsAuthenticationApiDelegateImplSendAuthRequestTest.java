package ag.act.handler;

import ag.act.facade.auth.SmsAuthFacade;
import ag.act.model.AuthUserResponse;
import ag.act.model.SendAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class SmsAuthenticationApiDelegateImplSendAuthRequestTest {

    @InjectMocks
    private SmsAuthenticationApiDelegateImpl delegate;
    @Mock
    private SmsAuthFacade smsAuthFacade;

    @Nested
    class SendAuthRequest {

        @Mock
        private ag.act.model.SendAuthRequest sendAuthRequest;
        @Mock
        private ag.act.model.SendAuthResponse sendAuthResponse;

        @Test
        void shouldSendMobileAuthRequest() {

            // Given
            given(smsAuthFacade.sendAuthRequest(sendAuthRequest)).willReturn(sendAuthResponse);

            // When
            final ResponseEntity<SendAuthResponse> response = delegate.sendAuthRequest(sendAuthRequest);

            // Then
            assertThat(response.getBody(), is(sendAuthResponse));
        }
    }

    @Nested
    class VerifyAuthCode {
        @Mock
        private ag.act.model.VerifyAuthCodeRequest verifyAuthCodeRequest;
        @Mock
        private ag.act.model.AuthUserResponse authUserResponse;

        @BeforeEach
        void setUp() {
            given(smsAuthFacade.verifyAuthCode(verifyAuthCodeRequest)).willReturn(authUserResponse);
        }

        @Test
        void shouldResultAuthUserResponse() {
            // When
            final ResponseEntity<AuthUserResponse> actual = delegate.verifyAuthCode(verifyAuthCodeRequest);

            // Then
            assertThat(actual.getBody(), is(authUserResponse));
        }
    }
}
