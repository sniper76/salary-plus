package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.CommentType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AutomatedAuthorPushesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/send-pushes";
    private static final String BATCH_NAME = "SEND_PUSHES";
    private static final int FIVE_MINUTES = 5;
    private List<MockedStatic<?>> statics;
    private Map<String, Object> request;
    private Post post;
    private Board board;
    private Stock stock;
    private User author;
    private String token;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        stubStock();
        board = itUtil.createBoard(stock);

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod, "periodTimeUnit", BatchRequest.PeriodTimeUnitEnum.MINUTE.name());

        String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusMinutes(batchPeriod)).willReturn(LocalDateTime.now());

        cleanUpAllPushes();
    }

    private void cleanUpAllPushes() {
        itUtil.deleteAllPushes();
    }

    private SimpleStringResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-api-key", "b0e6f688a1a08462201ef69f4")
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private void assertResponse(SimpleStringResponse result, int resultSize) {
        final String expectedResult = "[Batch] %s batch successfully finished. [sent: %s / %s]".formatted(BATCH_NAME, resultSize, resultSize);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow();
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertResponse(Push pushFromDatabase) {
        assertThat(pushFromDatabase.getSendStatus(), is(PushSendStatus.COMPLETE));
        assertThat(pushFromDatabase.getSentStartDatetime(), notNullValue());
        assertThat(pushFromDatabase.getSentEndDatetime(), notNullValue());
    }

    private void stubStock() {
        final String stockCode = someStockCode();
        final String stockName = someAlphanumericString(10);
        stock = itUtil.createStock(stockCode);
        stock.setName(stockName);
        stock = itUtil.updateStock(stock);
    }

    private void createAutomatedPushes(AutomatedPushCriteria automatedPushCriteria, int criteriaValue) {
        final Push push = itUtil.createPush(automatedPushCriteria.getMessage(), PushTargetType.AUTOMATED_AUTHOR, post.getId());
        push.setLinkType(AppLinkType.LINK);
        push.setSendStatus(PushSendStatus.READY);
        push.setTargetDatetime(LocalDateTime.now());
        itUtil.updatePush(push);
        itUtil.createAutomatedAuthorPush(
            post.getId(), push.getId(), criteriaValue, automatedPushCriteria, AutomatedPushContentType.POST
        );
    }

    private void assertFirebaseMessage() {
        then(firebaseMessagingService).should().sendPushNotification(
            any(CreateFcmPushDataDto.class),
            eq(List.of(token))
        );
    }

    @SuppressWarnings("unchecked")
    private void assertNoFirebasePushesSent() {
        then(firebaseMessagingService).should(never()).sendPushNotification(
            any(CreateFcmPushDataDto.class),
            any(List.class)
        );
    }

    private void refusePush() {
        UserPushAgreementGroupType.AUTHOR.getAgreementTypes()
            .forEach(agreementType -> itUtil.createUserPushAgreement(author, agreementType, false));
    }

    @Nested
    class WhenComments {
        private final int commentCount = 10;
        private final AutomatedPushCriteria automatedPushCriteria = AutomatedPushCriteria.COMMENT;

        @BeforeEach
        void setUp() {
            author = itUtil.createUser();
            token = someString(10, 20);
            author.setPushToken(token);
            itUtil.updateUser(author);
        }

        private void createPushAndComment() {
            final AutomatedPushCriteria automatedPushCriteria = AutomatedPushCriteria.COMMENT;
            for (int k = 1; k <= commentCount; k++) {
                final User user = itUtil.createUser();
                itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
                if (automatedPushCriteria.canSendPush(k)) {
                    createAutomatedPushes(automatedPushCriteria, k);
                }
            }
        }

        @Nested
        class AndRefusedPush {
            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, author.getId());
                createPushAndComment();
                refusePush();
            }

            @Test
            void shouldNotSend() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, 10);

                final List<Push> pushes = itUtil.findAllPushesByPostId(post.getId());
                pushes.forEach(it ->
                    assertResponse(itUtil.findPush(it.getId())
                        .orElseThrow(() -> new RuntimeException("[TEST] Push %s 찾을 수 없습니다.".formatted(it.getId()))))
                );

                assertNoFirebasePushesSent();
            }
        }

        @Nested
        class AndFindMultiPushes {

            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, author.getId());
                createPushAndComment();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldSuccess() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, 10);

                final List<Push> pushes = itUtil.findAllPushesByPostId(post.getId());
                pushes.forEach(it ->
                    assertResponse(itUtil.findPush(it.getId())
                        .orElseThrow(() -> new RuntimeException("[TEST] Push %s 찾을 수 없습니다.".formatted(it.getId()))))
                );

                assertFirebaseMessage();
            }
        }

        @Nested
        class AndHasAlreadyPushAfterCriteriaValue10Push {

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now().minusMinutes(FIVE_MINUTES);

                post = itUtil.createPost(board, author.getId());

                for (int index = 1; index <= commentCount; index++) {
                    itUtil.createComment(post.getId(), itUtil.createUser(), CommentType.POST, Status.ACTIVE);

                    if (!automatedPushCriteria.canSendPush(index)) {
                        continue;
                    }

                    final Push push = itUtil.createPush(automatedPushCriteria.getMessage(), PushTargetType.AUTOMATED_AUTHOR, post.getId());
                    push.setTargetDatetime(now.plusSeconds(index));
                    push.setLinkType(AppLinkType.LINK);

                    if (index == 10) {
                        push.setSendStatus(PushSendStatus.READY);
                        given(automatedAuthorPushSearchTimeFactory.getFiveMinuteAge()).willReturn(push.getTargetDatetime());
                    } else {
                        push.setSendStatus(PushSendStatus.COMPLETE);
                    }
                    itUtil.updatePush(push);
                    itUtil.createAutomatedAuthorPush(
                        post.getId(), push.getId(), index, automatedPushCriteria, AutomatedPushContentType.POST
                    );
                }
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldSuccess() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, 1);

                final List<Push> pushes = itUtil.findAllPushesByPostId(post.getId());
                pushes.forEach(it ->
                    assertResponse(itUtil.findPush(it.getId())
                        .orElseThrow(() -> new RuntimeException("[TEST] Push %s 찾을 수 없습니다.".formatted(it.getId()))))
                );

                assertFirebaseMessage();
            }
        }
    }

    @Nested
    class WhenLiked {
        private static final int likeCount = 10;
        private final AutomatedPushCriteria automatedPushCriteria = AutomatedPushCriteria.LIKE;

        @BeforeEach
        void setUp() {
            cleanUpAllPushes();
            author = itUtil.createUser();
            token = someString(10, 20);
            author.setPushToken(token);
            itUtil.updateUser(author);
        }

        @Nested
        class AndRefusedPush {
            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, author.getId());
                createPushAndPostUserLike();
                refusePush();
            }

            @Test
            void shouldNotSend() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, 1);

                final List<Push> pushes = itUtil.findAllPushesByPostId(post.getId());
                pushes.forEach(it ->
                    assertResponse(itUtil.findPush(it.getId())
                        .orElseThrow(() -> new RuntimeException("[TEST] Push %s 찾을 수 없습니다.".formatted(it.getId()))))
                );

                assertNoFirebasePushesSent();
            }
        }

        private void createPushAndPostUserLike() {
            for (int index = 1; index <= likeCount; index++) {
                final User user = itUtil.createUser();
                itUtil.createPostUserLike(user, post);
                if (automatedPushCriteria.canSendPush(index)) {
                    createAutomatedPushes(automatedPushCriteria, index);
                }
            }
        }

        @Nested
        class AndFindMultiPushes {
            @BeforeEach
            void setUp() {
                cleanUpAllPushes();
                post = itUtil.createPost(board, author.getId());
                createPushAndPostUserLike();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldSuccess() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, 1);

                final List<Push> pushes = itUtil.findAllPushesByPostId(post.getId());
                pushes.forEach(it ->
                    assertResponse(itUtil.findPush(it.getId())
                        .orElseThrow(() -> new RuntimeException("[TEST] Push %s 찾을 수 없습니다.".formatted(it.getId()))))
                );

                assertFirebaseMessage();
            }
        }
    }
}
