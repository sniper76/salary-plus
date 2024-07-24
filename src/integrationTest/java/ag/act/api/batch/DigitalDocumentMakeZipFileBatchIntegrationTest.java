package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.model.Status;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class DigitalDocumentMakeZipFileBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/digital-document/zip-file-request";
    private static final String BATCH_NAME = "DIGITAL_DOCUMENT_ZIP_FILE";

    private List<MockedStatic<?>> statics;

    private Map<String, Integer> request;
    private User user1;
    private User user2;
    private User user3;
    private Stock stock;
    private User acceptUser;
    private LocalDate referenceDate;
    private DigitalDocument digitalDocument1;
    private DigitalDocument digitalDocument2;
    private Post post1;
    private Post post2;
    @Captor
    private ArgumentCaptor<String> lambdaRequestBodyCaptor;
    private String postTitle;
    private String yyMMdd;
    private Boolean isTemp;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();
        itUtil.findAllDigitalDocuments()
            .forEach(it -> {
                it.setStatus(Status.DELETED);
                itUtil.updateDigitalDocument(it);
            });

        user1 = itUtil.createUser();
        user2 = itUtil.createUser();
        user3 = itUtil.createUser();

        stock = itUtil.createStock();
        final User postWriteUser = itUtil.createAdminUser();

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        postTitle = someAlphanumericString(10);
        post1 = itUtil.createPost(board, postWriteUser.getId(), postTitle, Boolean.FALSE);
        post2 = itUtil.createPost(board, postWriteUser.getId(), postTitle, Boolean.FALSE);

        acceptUser = createAcceptUserAsLeader();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final LocalDateTime now = KoreanDateTimeUtil.getNowInKoreanTime().toLocalDateTime();
        // 1) 시작일에 가까운 케이스
        // 1/17 => 1/16 ~ 1/31
        final LocalDateTime sTime1 = now.minusDays(1);
        final LocalDateTime eTime1 = now.plusDays(someIntegerBetween(6, 10));
        // 2) 종료일에 가까움 케이스
        // 1/17 => 1/1 ~ 1/16
        final LocalDateTime sTime2 = now.minusDays(someIntegerBetween(6, 10));
        final LocalDateTime eTime2 = now.minusDays(1);

        digitalDocument1 = createDigitalDocument(post1, stock, sTime1, eTime1);
        digitalDocument2 = createDigitalDocument(post2, stock, sTime2, eTime2);
        final DigitalDocument digitalDocumentAlreadyEnd = createDigitalDocument(post1, stock, now.minusDays(3), now.minusDays(2));
        final DigitalDocument digitalDocumentNotStarted = createDigitalDocument(post2, stock, now.plusDays(2), now.plusDays(3));
        final List<DigitalDocumentItem> digitalDocumentItemList1 = itUtil.createDigitalDocumentItemList(digitalDocument1);
        final List<DigitalDocumentItem> digitalDocumentItemList2 = itUtil.createDigitalDocumentItemList(digitalDocument2);

        itUtil.createUserHoldingStock(stock.getCode(), user1);
        itUtil.createUserHoldingStock(stock.getCode(), user2);
        itUtil.createUserHoldingStock(stock.getCode(), user3);

        createDigitalDocumentUserAndAnswer(user1, stock, digitalDocument1, digitalDocumentItemList1);
        createDigitalDocumentUserAndAnswer(user2, stock, digitalDocument1, digitalDocumentItemList1);
        createDigitalDocumentUserAndAnswer(user3, stock, digitalDocument1, digitalDocumentItemList1);

        createDigitalDocumentUserAndAnswer(user1, stock, digitalDocument2, digitalDocumentItemList2);
        createDigitalDocumentUserAndAnswer(user2, stock, digitalDocument2, digitalDocumentItemList2);
        createDigitalDocumentUserAndAnswer(user3, stock, digitalDocument2, digitalDocumentItemList2);

        final int batchPeriod = 0;
        request = Map.of("batchPeriod", batchPeriod);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(someString(5));
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(now);
        yyMMdd = someAlphanumericString(10);
        given(DateTimeUtil.getFormattedCurrentTimeInKorean("yyMMdd")).willReturn(yyMMdd);
        isTemp = someBoolean();
        given(DateTimeUtil.isNowAfter(any(LocalDateTime.class))).willReturn(!isTemp);

        // S3 람다는 Mocking 처리한다.
        willDoNothing().given(lambdaService).invokeLambdaAsync(anyString(), anyString());
    }

    private void createDigitalDocumentUserAndAnswer(
        User user, Stock stock, DigitalDocument document, List<DigitalDocumentItem> items
    ) {
        final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        final DigitalDocumentUser documentUser = itUtil.createDigitalDocumentUser(
            document, user, stock, pdfPath, DigitalDocumentAnswerStatus.SAVE
        );
        itUtil.createDigitalDocumentItemUserAnswerList(user.getId(), items);
        itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
    }

    private DigitalDocument createDigitalDocument(Post post, Stock stock, LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
        );
        post.setDigitalDocument(digitalDocument);

        return digitalDocument;
    }

    private User createAcceptUserAsLeader() {
        final User acceptUser = itUtil.createAcceptorUser();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());

        return acceptUser;
    }

    private void assertLambdaRequest() {
        then(lambdaService).should(times(2)).invokeLambdaAsync(anyString(), lambdaRequestBodyCaptor.capture());

        final List<DigitalDocument> digitalDocumentList = List.of(digitalDocument1, digitalDocument2);

        for (int i = 0; i < digitalDocumentList.size(); i++) {
            final String lambdaRequest = lambdaRequestBodyCaptor.getAllValues().get(i);
            final DigitalDocument digitalDocument = digitalDocumentList.get(i);
            final String zipFileName = getZipFileName(postTitle);
            assertThat(lambdaRequest,
                containsString("\"destinationDirectory\":\"contents/digitaldocument/%s/destination\"".formatted(digitalDocument.getId())));
            assertThat(lambdaRequest, containsString("\"zipFilename\":\"%s\"".formatted(zipFileName)));
            assertThat(lambdaRequest, containsString("\"fileType\":\"DIGITAL_DOCUMENT\""));

            assertThat(lambdaRequest, containsString("\"password\":\"%s\"".formatted(stock.getCode())));
        }
    }

    private void assertDigitalDocumentDownloadInDatabase(Long digitalDocumentId) {

        final List<DigitalDocumentDownload> digitalDocumentDownloadList = itUtil.findAllDigitalDocumentDownload(digitalDocumentId);

        assertThat(digitalDocumentDownloadList.size(), is(1));

        assertThat(digitalDocumentDownloadList.get(0).getDigitalDocumentId(), is(digitalDocumentId));
        assertThat(digitalDocumentDownloadList.get(0).getRequestUserId(), is(ActUserProvider.getSystemUserId()));
        assertThat(digitalDocumentDownloadList.get(0).getIsLatest(), is(true));
        assertThat(digitalDocumentDownloadList.get(0).getZipFileStatus(), is(ZipFileStatus.IN_PROGRESS));
        assertThat(digitalDocumentDownloadList.get(0).getDownloadCount(), is(0));

        final DigitalDocumentDownload actual = digitalDocumentDownloadList.get(0);
        final DigitalDocumentDownload expected = itUtil.findDigitalDocument(digitalDocumentId).getLatestDigitalDocumentDownload()
            .orElseThrow();
        assertThat(actual.getId(), equalTo(expected.getId()));
        assertThat(actual.getDigitalDocumentId(), equalTo(expected.getDigitalDocumentId()));
        assertThat(actual.getRequestUserId(), equalTo(expected.getRequestUserId()));
        assertThat(actual.getIsLatest(), equalTo(expected.getIsLatest()));
        assertThat(actual.getZipFileStatus(), equalTo(expected.getZipFileStatus()));
        assertThat(actual.getDownloadCount(), equalTo(expected.getDownloadCount()));

    }

    private String getZipFileName(String postTitle) {
        return "%s_%s_%s.zip".formatted(stock.getName(), postTitle + (isTemp ? "_temp" : ""), yyMMdd);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldCleanupTheUnfinishedDigitalDocumentUsersBeforeOneDay() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(batchXApiKey())
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertResponse(result);
        assertDigitalDocumentDownloadInDatabase(digitalDocument1.getId());
        assertDigitalDocumentDownloadInDatabase(digitalDocument2.getId());
        assertLambdaRequest();
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [requested: %s / %s]".formatted(BATCH_NAME, 2, 2);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }
}
