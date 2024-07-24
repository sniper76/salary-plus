package ag.act;

import ag.act.core.configuration.DevServerFeatureFlagUtil;
import ag.act.core.infra.S3Environment;
import ag.act.core.infra.ServerEnvironment;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.SlackChannel;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.itutil.ITUtil;
import ag.act.itutil.authentication.AuthenticationTestUtil.TestHeader;
import ag.act.itutil.dbcleaner.DbCleaner;
import ag.act.module.cache.AppPreferenceCache;
import ag.act.module.dart.DartCorporationPageableFactory;
import ag.act.module.dart.DartHttpUriBuilderFactory;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentNumberGenerator;
import ag.act.module.digitaldocumentgenerator.datetimeprovider.DigitalDocumentFillCurrentDateTimeProvider;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark.PDFWaterMarkCurrentDateTimeProvider;
import ag.act.module.krx.KrxHttpClientUtil;
import ag.act.module.markany.dna.MarkAnyDNAClient;
import ag.act.module.modusign.ModuSignHttpClientUtil;
import ag.act.module.okcert.OkCertClient;
import ag.act.module.stocksearchrecommendation.StockSearchRecommendationBaseDateTimeProvider;
import ag.act.service.FirebaseMessagingService;
import ag.act.service.aws.LambdaService;
import ag.act.service.aws.SesService;
import ag.act.service.image.ImageIOWrapper;
import ag.act.service.image.ScalrClient;
import ag.act.service.push.AutomatedAuthorPushSearchTimeFactory;
import ag.act.service.recaptcha.RecaptchaVerifier;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.ObjectMapperUtil;
import ag.act.util.SlackMessageSender;
import ag.act.util.ZoneIdUtil;
import org.apache.commons.lang3.IntegerRange;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@SuppressWarnings({"unused", "checkstyle:ParameterName"})
@SpringIntegrationTest
public abstract class AbstractCommonIntegrationTest {
    protected static final int PUSH_SAFE_TIME_RANGE_OFFSET = 2;
    protected static final int MIN_HOUR = 0;
    protected static final int MAX_HOUR = 23;
    protected static final Integer PAGE_1 = 1;
    protected static final Integer PAGE_2 = 2;
    protected static final Integer PAGE_3 = 3;
    protected static final Integer PAGE_4 = 4;
    protected static final Integer PAGE_5 = 5;
    protected static final String CREATED_AT_DESC = "createdAt:DESC";
    protected static final String X_API_KEY = "x-api-key";
    protected static final String DEFAULT_BATCH_API_KEY = "b0e6f688a1a08462201ef69f4";
    protected static final String X_APP_VERSION = "x-app-version";
    protected static final String X_APP_VERSION_CMS = "CMS";
    protected static final String X_APP_VERSION_WEB = "WEB";
    protected static final String USER_AGENT = "User-Agent";
    protected static final String USER_AGENT_NOT_WEB = "Act/test";
    protected static final String USER_AGENT_WEB = "Mozilla/test";
    protected static Integer SIZE = 2;
    protected static final int KOREAN_TIME_OFFSET = 9;

    protected static final int BAD_REQUEST_STATUS = HttpStatus.BAD_REQUEST.value();
    protected static final int NOT_FOUND_STATUS = HttpStatus.NOT_FOUND.value();
    protected static final int UNAUTHORIZED_STATUS = HttpStatus.UNAUTHORIZED.value();
    protected static final int FORBIDDEN_STATUS = HttpStatus.FORBIDDEN.value();
    protected static final int INTERNAL_SERVER_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();
    protected static final int GONE_STATUS = HttpStatus.GONE.value();

    @MockBean
    protected OkCertClient okCertClient;
    @MockBean
    protected LambdaService lambdaService;
    @MockBean
    protected DefaultHttpClientUtil defaultHttpClientUtil;
    @MockBean
    protected FirebaseMessagingService firebaseMessagingService;
    @MockBean
    protected KrxHttpClientUtil krxHttpClientUtil;
    @MockBean
    protected DartHttpUriBuilderFactory dartHttpUriBuilderFactory;
    @MockBean
    protected ModuSignHttpClientUtil moduSignHttpClientUtil;
    @MockBean
    protected SlackMessageSender slackMessageSender;
    @MockBean
    protected MarkAnyDNAClient markAnyDNAClient;
    @MockBean
    protected AppRenewalDateProvider appRenewalDateProvider;
    @MockBean
    protected ServerEnvironment serverEnvironment;
    @MockBean
    protected AutomatedAuthorPushSearchTimeFactory automatedAuthorPushSearchTimeFactory;
    @MockBean
    protected DartCorporationPageableFactory dartCorporationPageableFactory;
    @MockBean
    protected ScalrClient scalrClient;
    @MockBean
    protected ImageIOWrapper imageIOWrapper;
    @MockBean
    protected StockSearchRecommendationBaseDateTimeProvider stockSearchRecommendationBaseDateTimeProvider;
    @MockBean
    protected DigitalDocumentNumberGenerator digitalDocumentNumberGenerator;
    @MockBean
    protected PDFWaterMarkCurrentDateTimeProvider pdfWaterMarkCurrentDateTimeProvider;
    @MockBean
    protected DigitalDocumentFillCurrentDateTimeProvider digitalDocumentFillCurrentDateTimeProvider;
    @MockBean
    protected AppPreferenceCache appPreferenceCache;
    @MockBean
    protected SesService sesService;
    @MockBean
    protected RecaptchaVerifier recaptchaVerifier;

    @Value("${external.dart.api-key}")
    protected String dartApiKey;
    @Value("${external.dart.company-api-url}")
    protected String companyApiUrl;
    @Value("${external.dart.corp-code-api-url}")
    protected String corpCodeApiUrl;

    @Autowired
    protected ITUtil itUtil;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapperUtil objectMapperUtil;
    @Autowired
    protected DbCleaner dbCleaner;
    @Autowired
    protected S3Environment s3Environment;
    @Autowired
    protected DevServerFeatureFlagUtil devServerFeatureFlagUtil;

    @Mock
    private BufferedImage bufferedImage;
    @Mock
    private ByteArrayOutputStream byteArrayOutputStream;
    @Mock
    private InputStream inputStream;
    protected static final LocalDate APP_RENEWAL_LOCALDATE = LocalDate.now().minusYears(someIntegerBetween(2, 5));

    protected MockMultipartFile hectoEncryptedStringFile;
    @Value("classpath:/digitaldocument/hecto/encrypted-hecto-sample-VmjC0IRC5kGoCIvxUFcv5FGXRbf9YYyB.pdf")
    private Resource hectoEncryptedPdf;

    @BeforeEach
    void commonSetUp() {
        given(appRenewalDateProvider.get()).willReturn(APP_RENEWAL_LOCALDATE);
        given(appRenewalDateProvider.getStartOfDay()).willReturn(APP_RENEWAL_LOCALDATE.atStartOfDay());
        given(serverEnvironment.getActEnvironment()).willReturn("TEST");
        given(automatedAuthorPushSearchTimeFactory.getFiveMinuteAge()).willReturn(LocalDateTime.now().minusMinutes(5));
        willDoNothing().given(slackMessageSender).sendSlackMessage(anyString(), any(SlackChannel.class));
        given(stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForSearchTrend()).willReturn(someAlphanumericString(10));
        given(stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForStockRanking(anyList())).willReturn(someAlphanumericString(10));

        setUpImageResizeMocks();
        setUpAppPreferenceCacheMocks();
        setUpPushSafeTimeRangeMocks();
        setUpDigitalDocumentMocks();
    }

    private void setUpDigitalDocumentMocks() {
        given(markAnyDNAClient.makeDna(any(byte[].class))).willAnswer(invocation -> invocation.getArgument(MIN_HOUR));
        given(digitalDocumentNumberGenerator.generate(any(DigitalDocumentType.class), anyLong(), anyLong()))
            .willReturn("ACT-C-1-231030-1호");
        given(pdfWaterMarkCurrentDateTimeProvider.getKoreanDateTime())
            .willReturn(
                ZonedDateTime.of(2023, 10, 30, 17, 0, 0, 0, ZoneIdUtil.getSeoulZoneId())
            );
        given(digitalDocumentFillCurrentDateTimeProvider.getKoreanDateTime())
            .willReturn(
                ZonedDateTime.of(2023, 10, 30, 17, 0, 0, 0, ZoneIdUtil.getSeoulZoneId())
            );
    }

    protected void setUpAppPreferenceCacheMocks() {
        Mockito.reset(appPreferenceCache);
        Arrays.stream(AppPreferenceType.values())
            .forEach(
                appPreferenceType -> given(appPreferenceCache.getValue(appPreferenceType))
                    .willReturn(appPreferenceType.getValue(appPreferenceType.getDefaultValue()))
            );
        mockCommentRestrictionIntervalSeconds();
    }

    private void mockCommentRestrictionIntervalSeconds() {
        given(appPreferenceCache.getValue(AppPreferenceType.COMMENT_RESTRICTION_INTERVAL_SECONDS))
            .willReturn(0);
    }

    private void setUpImageResizeMocks() {
        given(imageIOWrapper.read(any(InputStream.class))).willReturn(Optional.of(bufferedImage));
        given(imageIOWrapper.write(any(BufferedImage.class), anyString())).willReturn(Optional.of(byteArrayOutputStream));
        given(byteArrayOutputStream.toByteArray()).willReturn(new byte[MIN_HOUR]);
        given(byteArrayOutputStream.size()).willReturn(someIntegerBetween(10, 1000));
        given(scalrClient.resize(any(), anyInt())).willReturn(bufferedImage);
        given(bufferedImage.getWidth()).willReturn(someIntegerBetween(201, 1000));

        given(itUtil.getS3ServiceMockBean().getBaseUrlWithTailingSlash()).willReturn(s3Environment.getBaseUrl() + "/");
        given(itUtil.getS3ServiceMockBean().getBaseUrl()).willReturn(s3Environment.getBaseUrl());

        given(itUtil.getS3ServiceMockBean().findObjectFromPublicBucket(anyString())).willReturn(Optional.of(inputStream));
        given(itUtil.getS3ServiceMockBean().putObject(any(UploadFilePathDto.class), any(InputStream.class))).willReturn(true);
        given(itUtil.getS3ServiceMockBean().isActImage(anyString()))
            .willAnswer(invocation ->
                Optional.ofNullable((String) invocation.getArgument(MIN_HOUR))
                    .map(url -> url.startsWith(s3Environment.getBaseUrl()))
                    .orElse(false));
    }

    protected void mockServerEnvironmentIsProd(Boolean isProd) {
        given(serverEnvironment.isProd()).willReturn(isProd);
        devServerFeatureFlagUtil.init();
    }

    protected MockMultipartFile getHectoEncryptedStringFile() {
        try {
            byte[] hectoEncryptedStringBytes = Files.readAllBytes(hectoEncryptedPdf.getFile().toPath());
            return new MockMultipartFile(
                "hectoEncryptedBankAccountPdf",
                "encrypted-hecto-sample-VmjC0IRC5kGoCIvxUFcv5FGXRbf9YYyB.pdf",
                MediaType.TEXT_PLAIN_VALUE,
                hectoEncryptedStringBytes
            );
        } catch (IOException e) {
            throw new UnsupportedOperationException();
        }
    }

    private void setUpPushSafeTimeRangeMocks() {
        final ZonedDateTime currentDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneIdUtil.getSeoulZoneId());

        final int currentHour = currentDateTime.getHour();
        final int minHour = currentHour - PUSH_SAFE_TIME_RANGE_OFFSET;
        final int maxHour = currentHour + PUSH_SAFE_TIME_RANGE_OFFSET;

        given(appPreferenceCache.getValue(AppPreferenceType.PUSH_SAFE_TIME_RANGE_IN_HOURS))
            .willReturn(IntegerRange.of(
                Math.max(minHour, MIN_HOUR),
                Math.min(maxHour, MAX_HOUR)
            ));
    }

    protected HttpHeaders headers(TestHeader... testHeaders) {
        HttpHeaders headers = new HttpHeaders();

        Arrays.stream(testHeaders)
            .forEach(testHeader -> headers.add(testHeader.headerName(), testHeader.headerValue()));

        return headers;
    }

    protected HttpHeaders batchXApiKey() {
        HttpHeaders headers = new HttpHeaders();
        setXApiKeyHeader(DEFAULT_BATCH_API_KEY, headers);

        return headers;
    }

    private void setJwtHeader(String jwt, HttpHeaders headers) {
        Objects.requireNonNull(jwt, "jwt must not be null");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
    }

    private void setXAppVersionHeader(String xAppVersion, HttpHeaders headers) {
        Objects.requireNonNull(xAppVersion, "xAppVersion must not be null");
        headers.add(X_APP_VERSION, xAppVersion);
    }

    private void setUserAgentHeader(String userAgent, HttpHeaders headers) {
        Objects.requireNonNull(userAgent, "userAgent must not be null");
        headers.add(USER_AGENT, userAgent);
    }

    private void setXApiKeyHeader(String xApiKey, HttpHeaders headers) {
        Objects.requireNonNull(xApiKey, "xApiKey must not be null");
        headers.add(X_API_KEY, xApiKey);
    }
}
