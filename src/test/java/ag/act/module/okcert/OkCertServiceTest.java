package ag.act.module.okcert;

import ag.act.exception.InternalServerException;
import ag.act.module.okcert.converter.OkCertRequestConverter;
import ag.act.module.okcert.converter.OkCertResponseConverter;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import kcb.module.v3.exception.OkCertException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class OkCertServiceTest {

    @InjectMocks
    private OkCertService service;

    @Mock
    private OkCertClient okCertClient;
    @Mock
    private IOkCertResourceManager okCertResourceManager;
    @Mock
    private OkCertRequestConverter okCertRequestConverter;
    @Mock
    private OkCertResponseConverter okCertResponseConverter;
    private String target;
    private String cpCd;
    private String requestServiceName;
    private String responseServiceName;
    private String siteName;
    private String siteUrl;
    private String okCertLicencePath;
    private String body;
    private String response;

    @BeforeEach
    void setUp() throws OkCertException {
        target = someString(5);
        cpCd = someString(7);
        requestServiceName = someString(9);
        responseServiceName = someString(11);
        siteName = someString(13);
        siteUrl = someString(15);
        okCertLicencePath = someString(17);
        body = someString(20);
        response = someString(25);

        given(okCertResourceManager.getSiteName()).willReturn(siteName);
        given(okCertResourceManager.getSiteUrl()).willReturn(siteUrl);
        given(okCertResourceManager.getReqSvcName()).willReturn(requestServiceName);
        given(okCertResourceManager.getResSvcName()).willReturn(responseServiceName);
        given(okCertResourceManager.getTarget()).willReturn(target);
        given(okCertResourceManager.getCpCd()).willReturn(cpCd);
        given(okCertResourceManager.getOkCertLicencePath()).willReturn(okCertLicencePath);
        given(okCertClient.callOkCert(target, cpCd, requestServiceName, okCertLicencePath, someString(5))).willReturn(someString(5));
    }

    @Nested
    class OkCertSendRequest {

        @Mock
        private ag.act.module.okcert.dto.OkCertSendRequest okCertSendRequest;
        @Mock
        private OkCertSendResponse okCertSendResponse;
        private OkCertSendResponse actualResponse;

        @BeforeEach
        void setUp() throws OkCertException, ReflectiveOperationException, JsonProcessingException {
            given(okCertRequestConverter.convert(okCertSendRequest)).willReturn(body);
            given(okCertClient.callOkCert(target, cpCd, requestServiceName, okCertLicencePath, body)).willReturn(response);
            given(okCertResponseConverter.convert(response, OkCertSendResponse.class)).willReturn(okCertSendResponse);

            actualResponse = service.okCertSendRequest(okCertSendRequest);
        }

        @Test
        void shouldReturnOkCertSendResponse() {
            assertThat(actualResponse, is(okCertSendResponse));
        }

        @Test
        void shouldSetSiteName() {
            then(okCertSendRequest).should().setSite_name(siteName);
        }

        @Test
        void shouldSetSiteUrl() {
            then(okCertSendRequest).should().setSite_url(siteUrl);
        }

        @Test
        void shouldCallOkCertClient() throws OkCertException {
            then(okCertClient).should().callOkCert(target, cpCd, requestServiceName, okCertLicencePath, body);
        }
    }

    @Nested
    class OkCertResendRequest {

        @Mock
        private ag.act.module.okcert.dto.OkCertResendRequest okCertResendRequest;
        @Mock
        private OkCertResendResponse okCertResendResponse;
        private OkCertResendResponse actualResponse;

        @BeforeEach
        void setUp() throws OkCertException, ReflectiveOperationException, JsonProcessingException {
            given(okCertRequestConverter.convert(okCertResendRequest)).willReturn(body);
            given(okCertClient.callOkCert(target, cpCd, requestServiceName, okCertLicencePath, body)).willReturn(response);
            given(okCertResponseConverter.convert(response, OkCertResendResponse.class)).willReturn(okCertResendResponse);

            actualResponse = service.okCertResendRequest(okCertResendRequest);
        }

        @Test
        void shouldReturnOkCertSendResponse() {
            assertThat(actualResponse, is(okCertResendResponse));
        }

        @Test
        void shouldSetSmsResendYn_Y() {
            then(okCertResendRequest).should().setSms_resend_yn("Y");
        }

        @Test
        void shouldCallOkCertClient() throws OkCertException {
            then(okCertClient).should().callOkCert(target, cpCd, requestServiceName, okCertLicencePath, body);
        }
    }

    @Nested
    class OkCertVerifyAuth {

        @Mock
        private ag.act.module.okcert.dto.OkCertVerifyRequest okCertVerifyRequest;
        @Mock
        private OkCertVerifyResponse okCertVerifyResponse;
        private OkCertVerifyResponse actualResponse;

        @BeforeEach
        void setUp() throws OkCertException, ReflectiveOperationException, JsonProcessingException {
            given(okCertRequestConverter.convert(okCertVerifyRequest)).willReturn(body);
            given(okCertClient.callOkCert(target, cpCd, responseServiceName, okCertLicencePath, body)).willReturn(response);
            given(okCertResponseConverter.convert(response, OkCertVerifyResponse.class)).willReturn(okCertVerifyResponse);

            actualResponse = service.okCertVerifyAuth(okCertVerifyRequest);
        }

        @Test
        void shouldReturnOkCertSendResponse() {
            assertThat(actualResponse, is(okCertVerifyResponse));
        }

        @Test
        void shouldCallOkCertClient() throws OkCertException {
            then(okCertClient).should().callOkCert(target, cpCd, responseServiceName, okCertLicencePath, body);
        }
    }


    @Nested
    class WhenOkCertException {

        @Mock
        private ag.act.module.okcert.dto.OkCertSendRequest okCertSendRequest;

        @BeforeEach
        void setUp() throws OkCertException {
            given(okCertClient.callOkCert(target, cpCd, requestServiceName, okCertLicencePath, body)).willReturn(response);
            given(okCertRequestConverter.convert(okCertSendRequest)).willReturn(body);
        }

        @Test
        void shouldThrowExceptionWhenOkCertExceptionOccurred() throws OkCertException {
            // Given
            given(okCertClient.callOkCert(any(), any(), any(), any(), any())).willThrow(OkCertException.class);

            // When
            assertException(
                InternalServerException.class,
                () -> service.okCertSendRequest(okCertSendRequest),
                "휴대폰 인증서버 호출 중에 오류가 발생하였습니다."
            );
        }

        @Test
        void shouldThrowExceptionWhenReflectiveOperationExceptionOccurred() throws ReflectiveOperationException, JsonProcessingException {
            // Given
            given(okCertResponseConverter.convert(response, OkCertSendResponse.class)).willThrow(ReflectiveOperationException.class);

            // When // Then
            assertException(
                InternalServerException.class,
                () -> service.okCertSendRequest(okCertSendRequest),
                "휴대폰 인증서버 응답을 처리하는 중에 오류가 발생하였습니다."
            );
        }

        @Test
        void shouldThrowExceptionWhenJsonProcessingExceptionOccurred() throws ReflectiveOperationException, JsonProcessingException {
            // Given
            given(okCertResponseConverter.convert(response, OkCertSendResponse.class)).willThrow(JsonProcessingException.class);

            // When // Then
            assertException(
                InternalServerException.class,
                () -> service.okCertSendRequest(okCertSendRequest),
                "휴대폰 인증서버 응답을 처리하는 중에 오류가 발생하였습니다."
            );
        }
    }
}
