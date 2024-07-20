package ag.act.external.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgssoft.httpclient.HttpClientMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultHttpClientUtilTest {

    @Mock
    private ObjectMapper objectMapper;

    private String encodedRequestUrl;
    private URI requestUri;
    private String requestHeaderKeyOne;
    private String requestHeaderValueOne;
    private String requestHeaderKeyTwo;
    private String requestHeaderValueTwo;
    private Map<String, String> requestHeaders;
    private HttpClientMock httpClientMock;
    private HttpClientUtil httpClientUtil;
    private Integer responseStatusCode;
    private String responseBody;
    private HttpResponse<String> response;

    @BeforeEach
    void setUp() {
        String requestUrl = "http://localhost:8080/search?q=chief information officer|information technology";
        requestUri = ((UriBuilder) UriComponentsBuilder.fromHttpUrl(requestUrl)).build();
        encodedRequestUrl = requestUri.toString();
        requestHeaderKeyOne = "Content-Type";
        requestHeaderValueOne = someAlphanumericString(7);
        requestHeaderKeyTwo = "Accept";
        requestHeaderValueTwo = someAlphanumericString(10);
        requestHeaders = Map.of(
            requestHeaderKeyOne, requestHeaderValueOne,
            requestHeaderKeyTwo, requestHeaderValueTwo
        );

        httpClientMock = new HttpClientMock();
        httpClientUtil = new DefaultHttpClientUtil(objectMapper, httpClientMock);

        responseStatusCode = someInteger();
        responseBody = someString(5);
    }

    @Nested
    class SendPostRequest {
        private Map<String, Object> requestPayload;
        private String serializeRequestPayload;

        @BeforeEach
        void setUp() throws Exception {
            requestPayload = new HashMap<>();
            requestPayload.put(someString(10), someString(15));
            serializeRequestPayload = someString(20);

            httpClientMock.onPost().doReturn(responseStatusCode, responseBody);

            when(objectMapper.writeValueAsString(anyMap())).thenReturn(serializeRequestPayload);
        }

        @Nested
        class WithRequestHeaders extends PostRequestCommonTestCases {

            @BeforeEach
            void setUp() throws Exception {
                response = httpClientUtil.post(requestUri, requestPayload, requestHeaders);
            }

            @Test
            void shouldSendPostRequest() {
                httpClientMock.verify().post(encodedRequestUrl)
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Accept", "application/json")
                    .withHeader(requestHeaderKeyOne, requestHeaderValueOne)
                    .withHeader(requestHeaderKeyTwo, requestHeaderValueTwo)
                    .withBody(equalTo(serializeRequestPayload))
                    .called();
            }

        }

        @Nested
        class WithoutRequestHeaders extends PostRequestCommonTestCases {

            @BeforeEach
            void setUp() throws Exception {
                response = httpClientUtil.post(requestUri, requestPayload);
            }

            @Test
            void shouldSendPostRequest() {
                httpClientMock.verify().post(encodedRequestUrl)
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Accept", "application/json")
                    .withBody(equalTo(serializeRequestPayload))
                    .called();
            }

        }

        @SuppressWarnings("unused")
        class PostRequestCommonTestCases extends RequestCommonTestCases {

            @Test
            void shouldSerializeRequestPayload() throws JsonProcessingException {
                verify(objectMapper).writeValueAsString(requestPayload);
            }

        }

    }

    @SuppressWarnings("unused")
    class RequestCommonTestCases {

        @Test
        void shouldReturnResponseStatusCode() {
            assertThat(response.statusCode(), equalTo(responseStatusCode));
        }

        @Test
        void shouldReturnResponseBody() {
            assertThat(response.body(), equalTo(responseBody));
        }

    }
}
