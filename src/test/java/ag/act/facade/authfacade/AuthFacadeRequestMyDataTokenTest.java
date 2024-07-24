package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.converter.mydata.MyDataTokenResponseConverter;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.facade.auth.AuthFacade;
import ag.act.module.mydata.MyDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFacadeRequestMyDataTokenTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private MyDataTokenResponseConverter myDataTokenResponseConverter;
    @Mock
    private MyDataService myDataService;
    @Mock
    private CryptoHelper cryptoHelper;
    @Mock
    private ag.act.model.MyDataTokenResponse myDataTokenResponse;
    @Mock
    private User user;
    private ag.act.model.MyDataTokenResponse result;
    private String finpongAccessToken;
    private String hashedCI;
    private String ci;
    private String hashedPhoneNumber;
    private String phoneNumber;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(ActUserProvider.class));

        // Given
        accessToken = someString(5);
        finpongAccessToken = someString(7);
        hashedCI = someString(10);
        ci = someString(13);
        hashedPhoneNumber = someString(15);
        phoneNumber = someString(20);

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getHashedCI()).willReturn(hashedCI);
        given(user.getHashedPhoneNumber()).willReturn(hashedPhoneNumber);
        given(cryptoHelper.decrypt(hashedCI)).willReturn(ci);
        given(cryptoHelper.decrypt(hashedPhoneNumber)).willReturn(phoneNumber);
        given(myDataTokenResponseConverter.convert(finpongAccessToken)).willReturn(myDataTokenResponse);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class Success {
        @BeforeEach
        void setUp() throws JsonProcessingException {
            // Given
            given(myDataService.getFinpongAccessToken(user, ci, phoneNumber, accessToken))
                .willReturn(finpongAccessToken);

            // When
            result = facade.requestMyDataToken(accessToken);
        }

        @Test
        void shouldDecryptHashedCI() throws Exception {
            then(cryptoHelper).should().decrypt(hashedCI);
        }

        @Test
        void shouldDecryptHashedPhoneNumber() throws Exception {
            then(cryptoHelper).should().decrypt(hashedPhoneNumber);
        }

        @Test
        void shouldGetFinpongAccessToken() throws JsonProcessingException {
            then(myDataService).should().getFinpongAccessToken(user, ci, phoneNumber, accessToken);
        }

        @Test
        void shouldConvertFinpongAccessToken() {
            then(myDataTokenResponseConverter).should().convert(finpongAccessToken);
        }

        @Test
        void shouldReturnMyDataTokenResponse() {
            assertThat(result, is(myDataTokenResponse));
        }
    }

    @Nested
    class WhenJsonProcessingExceptionOccur {
        @BeforeEach
        void setUp() {
            // Given
            given(myDataService.getFinpongAccessToken(user, ci, phoneNumber, accessToken))
                .willThrow(RuntimeException.class);
        }

        @Test
        void shouldInternalServerException() {
            assertException(
                RuntimeException.class,
                () -> facade.requestMyDataToken(accessToken),
                "마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다."
            );
        }
    }

    @Nested
    class WhenActRuntimeExceptionOccur {

        private String errorMessage;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            errorMessage = someString(5);

            final ActRuntimeException actRuntimeException = new ActRuntimeException(errorMessage);

            given(myDataService.getFinpongAccessToken(user, ci, phoneNumber, accessToken))
                .willThrow(actRuntimeException);
        }

        @Test
        void shouldActRuntimeException() {
            assertException(
                ag.act.exception.ActRuntimeException.class,
                () -> facade.requestMyDataToken(accessToken),
                errorMessage
            );
        }
    }
}
