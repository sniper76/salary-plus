package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CleanupDigitalDocumentUsersBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/cleanup/unfinished-digital-document-users";
    private static final String BATCH_NAME = "CLEANUP_UNFINISHED_DIGITAL_DOCUMENT_USERS";

    private List<MockedStatic<?>> statics;

    private Map<String, Integer> request;
    private User user1;
    private User user2;
    private User user3;
    private Stock stock;
    private User acceptUser;
    private LocalDate referenceDate;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        // Prepare DigitalDocument Data
        user1 = itUtil.createUser();
        user2 = itUtil.createUser();
        user3 = itUtil.createUser();
        stock = itUtil.createStock();
        acceptUser = createAcceptUserAsLeader();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final DigitalDocument digitalDocument = createDigitalDocument();
        final List<DigitalDocumentItem> digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);

        createDigitalDocumentUserAndAnswer(user1, digitalDocument, digitalDocumentItemList, true);
        createDigitalDocumentUserAndAnswer(user2, digitalDocument, digitalDocumentItemList, true);
        createDigitalDocumentUserAndAnswer(user3, digitalDocument, digitalDocumentItemList, false);

        // Prepare Batch Data
        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(someString(5));
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private void createDigitalDocumentUserAndAnswer(
        User user, DigitalDocument document, List<DigitalDocumentItem> items, boolean isDeleteTarget
    ) {
        final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        itUtil.createUserHoldingStock(stock.getCode(), user);
        var documentUser = itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.SAVE);
        itUtil.createDigitalDocumentItemUserAnswerList(user.getId(), items);
        itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);

        given(DateTimeUtil.isBeforeInHours(eq(documentUser.getUpdatedAt()), anyInt())).willReturn(isDeleteTarget);
    }

    private DigitalDocument createDigitalDocument() {
        final User postWriteUser = itUtil.createAdminUser();
        final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(3);
        final LocalDateTime targetEndDate = LocalDateTime.now().plusDays(1);

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final Post post = itUtil.createPost(board, postWriteUser.getId());
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
        assertDeletedDataFromDatabase(user1);
        assertDeletedDataFromDatabase(user2);
        assertRemainingDataFromDatabase(user3);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [deletion: %s / %s]".formatted(BATCH_NAME, 2, 2);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertDeletedDataFromDatabase(User user) {
        assertThat(itUtil.findDigitalDocumentUsersByUserId(user.getId()).isEmpty(), is(true));
        assertThat(itUtil.findDigitalDocumentItemUserAnswersByUserId(user.getId()).isEmpty(), is(true));
    }

    private void assertRemainingDataFromDatabase(User user) {
        assertThat(itUtil.findDigitalDocumentUsersByUserId(user.getId()).isEmpty(), is(false));
        assertThat(itUtil.findDigitalDocumentItemUserAnswersByUserId(user.getId()).isEmpty(), is(false));
    }

}
