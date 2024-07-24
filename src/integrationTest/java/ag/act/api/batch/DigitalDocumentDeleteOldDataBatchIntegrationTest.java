package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.SimpleStringResponse;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class DigitalDocumentDeleteOldDataBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/digital-document/delete-old-documents";
    private static final String BATCH_NAME = "DELETE_OLD_DIGITAL_DOCUMENT";
    private static final String DUMMY_ACCEPTOR_NAME = "컨두잇";

    private List<MockedStatic<?>> statics;
    private Map<String, Integer> request;
    private Stock stock;
    private User acceptUser;
    private LocalDate referenceDate;
    private DigitalDocument digitalDocument1;
    private DigitalDocument digitalDocument2;
    @Captor
    private ArgumentCaptor<String> s3PathCaptor;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();
        itUtil.cleanUpAllPosts();

        stock = itUtil.createStock();
        final User postWriteUser = itUtil.createAdminUser();
        final String yyMMdd = someAlphanumericString(10);

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final String postTitle = someAlphanumericString(10);
        final Post post1 = itUtil.createPost(board, postWriteUser.getId(), postTitle, Boolean.FALSE);
        final Post post2 = itUtil.createPost(board, postWriteUser.getId(), postTitle, Boolean.FALSE);

        acceptUser = createAcceptUserAsLeader();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final LocalDateTime now = KoreanDateTimeUtil.getTodayLocalDate().atStartOfDay();
        // 1) 90days over 케이스
        final LocalDateTime sTime1 = now.minusDays(100);
        final LocalDateTime eTime1 = now.minusDays(92);
        // 2) 90days before 케이스
        final LocalDateTime sTime2 = now.minusDays(100);
        final LocalDateTime eTime2 = now.minusDays(88);

        digitalDocument1 = createDigitalDocument(post1, stock, sTime1, eTime1);
        digitalDocument2 = createDigitalDocument(post2, stock, sTime2, eTime2);

        final int batchPeriod = 0;
        request = Map.of("batchPeriod", batchPeriod);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(someString(5));
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(now);
        given(DateTimeUtil.getFormattedCurrentTimeInKorean("yyMMdd")).willReturn(yyMMdd);

        willDoNothing().given(itUtil.getS3ServiceMockBean()).deleteDirectoryInFiles(anyString());
    }

    private DigitalDocument createDigitalDocument(Post post, Stock stock, LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        final DigitalDocumentType digitalDocumentType = someThing(
            DigitalDocumentType.DIGITAL_PROXY,
            DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT
        );
        DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, digitalDocumentType, targetStartDate, targetEndDate, referenceDate
        );
        itUtil.createStockAcceptorUserHistory(stock.getCode(), acceptUser);
        post.setDigitalDocument(digitalDocument);

        return digitalDocument;
    }

    private User createAcceptUserAsLeader() {
        final User acceptUser = itUtil.createAcceptorUser();
        itUtil.createSolidarity(stock.getCode());

        return acceptUser;
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldBeSuccess() throws Exception {
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

        final SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );

        assertResponse(result);
        assertS3Path();
        assertDigitalDocumentStatus(digitalDocument1.getId(), Status.INACTIVE_BY_ADMIN);//target data
        assertDigitalDocumentStatus(digitalDocument2.getId(), Status.ACTIVE);
    }

    private void assertS3Path() {
        then(itUtil.getS3ServiceMockBean()).should().deleteDirectoryInFiles(s3PathCaptor.capture());
        assertThat(s3PathCaptor.getValue(), is("contents/digitaldocument/%s".formatted(digitalDocument1.getId())));
    }

    private void assertResponse(SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [requested: %s / %s]".formatted(BATCH_NAME, 1, 1);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow();
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertDigitalDocumentStatus(Long digitalDocumentId, Status status) {
        final DigitalDocument digitalDocumentDatabases = itUtil.findDigitalDocument(digitalDocumentId);
        final Post databasesPost = digitalDocumentDatabases.getPost();

        assertThat(databasesPost.getStatus(), is(status));
        assertStockAcceptorUserHistoryUpdatedWithDummyName(digitalDocumentDatabases);
    }

    private void assertStockAcceptorUserHistoryUpdatedWithDummyName(DigitalDocument digitalDocumentDatabases) {
        final String stockCode = digitalDocumentDatabases.getStockCode();
        final Long acceptUserId = digitalDocumentDatabases.getAcceptUserId();
        final StockAcceptorUserHistory stockAcceptorUserHistory = itUtil.findStockAcceptorUserHistory(stockCode, acceptUserId).orElseThrow();

        assertThat(stockAcceptorUserHistory.getName(), is(DUMMY_ACCEPTOR_NAME));
    }
}
