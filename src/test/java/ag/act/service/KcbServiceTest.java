package ag.act.service;

import ag.act.enums.ActErrorCode;
import ag.act.exception.KcbOkCertException;
import ag.act.module.okcert.OkCertService;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertResponseErrorMessage;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

@MockitoSettings(strictness = Strictness.LENIENT)
class KcbServiceTest {

    @InjectMocks
    private KcbService kcbService;
    @Mock
    private OkCertService okCertService;

    @Nested
    class SendAuthRequest {
        @Mock
        private OkCertSendRequest okCertSendRequest;
        @Mock
        private OkCertSendResponse okCertSendResponse;

        @BeforeEach
        void setUp() throws Exception {
            given(okCertService.okCertSendRequest(okCertSendRequest)).willReturn(okCertSendResponse);
            given(okCertSendResponse.getRsltCd()).willReturn("B000");
        }

        @Nested
        class WhenSendAuthRequestSuccess {

            @Test
            void shouldSendOkCertAuthRequest() {

                // When
                final OkCertSendResponse actual = kcbService.sendAuthRequest(okCertSendRequest);

                // Then
                assertThat(actual, is(okCertSendResponse));
            }
        }

        @Nested
        class WhenResultCodeIsFail {

            private OkCertResponseErrorMessage okCertResponseErrorMessage;

            @BeforeEach
            void setUp() {
                okCertResponseErrorMessage = someEnum(OkCertResponseErrorMessage.class);
                given(okCertSendResponse.getRsltCd()).willReturn(okCertResponseErrorMessage.name());
                given(okCertSendResponse.getRsltMsg()).willReturn(okCertResponseErrorMessage.getMessage());
            }

            @Test
            void shouldThrowOkCertException() {
                assertException(
                    KcbOkCertException.class,
                    () -> kcbService.sendAuthRequest(okCertSendRequest),
                    okCertResponseErrorMessage.getMessage(),
                    ActErrorCode.KCB_OK_CERT_FAILED
                );
            }
        }
    }

    @Nested
    class ResendAuthRequest {
        @Mock
        private OkCertResendRequest okCertResendRequest;
        @Mock
        private OkCertResendResponse okCertResendResponse;

        @BeforeEach
        void setUp() throws Exception {
            given(okCertService.okCertResendRequest(okCertResendRequest)).willReturn(okCertResendResponse);
            given(okCertResendResponse.getRsltCd()).willReturn("B000");
        }

        @Nested
        class WhenResendAuthRequestSuccess {

            @Test
            void shouldResendOkCertAuthRequest() {

                // When
                final OkCertResendResponse actual = kcbService.resendAuthRequest(okCertResendRequest);

                // Then
                assertThat(actual, is(okCertResendResponse));
            }
        }

        @Nested
        class WhenResultCodeIsFail {

            private OkCertResponseErrorMessage okCertResponseErrorMessage;

            @BeforeEach
            void setUp() {
                okCertResponseErrorMessage = someEnum(OkCertResponseErrorMessage.class);
                given(okCertResendResponse.getRsltCd()).willReturn(okCertResponseErrorMessage.name());
                given(okCertResendResponse.getRsltMsg()).willReturn(okCertResponseErrorMessage.getMessage());
            }

            @Test
            void shouldThrowOkCertException() {
                assertException(
                    KcbOkCertException.class,
                    () -> kcbService.resendAuthRequest(okCertResendRequest),
                    okCertResponseErrorMessage.getMessage(),
                    ActErrorCode.KCB_OK_CERT_FAILED
                );
            }
        }
    }

    @Nested
    class VerifyAuthRequest {
        @Mock
        private OkCertVerifyRequest okCertVerifyRequest;
        @Mock
        private OkCertVerifyResponse okCertVerifyResponse;

        @BeforeEach
        void setUp() throws Exception {
            given(okCertService.okCertVerifyAuth(okCertVerifyRequest)).willReturn(okCertVerifyResponse);
            given(okCertVerifyResponse.getRsltCd()).willReturn("B000");
        }

        @Nested
        class WhenVerifyAuthRequestSuccess {

            @Test
            void shouldVerifyOkCertAuthRequest() {

                // When
                final OkCertVerifyResponse actual = kcbService.verifyAuthCode(okCertVerifyRequest);

                // Then
                assertThat(actual, is(okCertVerifyResponse));
            }
        }

        @Nested
        class WhenResultCodeIsFail {

            private OkCertResponseErrorMessage okCertResponseErrorMessage;

            @BeforeEach
            void setUp() {
                okCertResponseErrorMessage = someEnum(OkCertResponseErrorMessage.class);
                given(okCertVerifyResponse.getRsltCd()).willReturn(okCertResponseErrorMessage.name());
                given(okCertVerifyResponse.getRsltMsg()).willReturn(okCertResponseErrorMessage.getMessage());
            }

            @Test
            void shouldThrowOkCertException() {
                assertException(
                    KcbOkCertException.class,
                    () -> kcbService.verifyAuthCode(okCertVerifyRequest),
                    okCertResponseErrorMessage.getMessage(),
                    ActErrorCode.KCB_OK_CERT_FAILED
                );
            }
        }
    }
}
