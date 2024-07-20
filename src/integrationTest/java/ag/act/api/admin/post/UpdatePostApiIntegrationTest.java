package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.image.ImageResponseConverter;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.notification.Notification;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.PushDetailsResponse;
import ag.act.model.PushRequest;
import ag.act.model.Status;
import ag.act.model.UpdatePostRequest;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static ag.act.TestUtil.TestHtmlContent;
import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someHtmlContentWithImages;
import static ag.act.TestUtil.someScript;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdatePostApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";
    private static final Boolean INACTIVE_NOTIFICATION = Boolean.FALSE;

    @Autowired
    private ImageResponseConverter imageResponseConverter;

    private String stockCode;
    private String adminJwt;
    private Board board;
    private Long postId;
    private final Boolean isAnonymous = Boolean.FALSE;
    private Post post;
    private LocalDate referenceDate;
    private LocalDateTime targetStartDate;
    private LocalDateTime targetEndDate;
    private UpdatePostRequest updatePostRequest;
    private Stock stock;

    private String expectedThumbnailImageUrl;
    private String content;
    private User adminUser;
    private int additionalPushTimeMinutes;
    private String title;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        User author = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), author);

        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
        targetStartDate = LocalDateTime.now().plusDays(1);
        targetEndDate = LocalDateTime.now().plusDays(5);

        post = createPostWithDigitalDocument(author, someString(10), board, stock);
        postId = post.getId();

        final FileContent image1 = itUtil.createImage();
        final FileContent image2 = itUtil.createImage();
        final String imageUrl1 = imageResponseConverter.convertImageUrl(image1);
        final String imageUrl2 = imageResponseConverter.convertImageUrl(image2);
        expectedThumbnailImageUrl = itUtil.getThumbnailImageUrl(imageUrl1);
        content = someHtmlContentWithImages(imageUrl1, imageUrl2);

        additionalPushTimeMinutes = appPreferenceCache.getValue(AppPreferenceType.ADDITIONAL_PUSH_TIME_MINUTES);
    }

    private UpdatePostRequest genRequest(Boolean active) {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle(someString(10));
        request.setContent(content + someScript());
        request.setIsEd(Boolean.TRUE);
        request.isAnonymous(isAnonymous);
        request.setIsActive(active);
        request.setIsNotification(someBoolean());

        final Instant endDate = Instant.now().plus(2, ChronoUnit.DAYS);

        ag.act.model.UpdatePostRequestDigitalDocument targetDateRequest = new ag.act.model.UpdatePostRequestDigitalDocument();
        targetDateRequest.setTargetEndDate(endDate);

        request.setDigitalDocument(targetDateRequest);

        return request;
    }

    private UpdatePostRequest genRequestForGlobalEvent(Boolean isActive, Boolean isNotification) {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setIsEd(Boolean.TRUE);
        request.isAnonymous(Boolean.FALSE);
        request.setIsActive(isActive);
        request.setIsNotification(isNotification);

        return request;
    }

    @Nested
    class WhenWithHtmlContent {
        private final Boolean active = Boolean.TRUE;
        private TestHtmlContent testHtmlContent;

        @BeforeEach
        void setUp() {
            updatePostRequest = genRequest(active);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

            final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                PostDetailsDataResponse.class
            );

            assertDocumentResponse(result, active);
        }
    }

    @Nested
    class WhenAdminUserUpdate {

        @Nested
        class AndAuthorIsAdmin {
            private final Boolean active = Boolean.TRUE;

            @BeforeEach
            void setUp() {
                updatePostRequest = genRequest(active);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertDocumentResponse(result, active);
            }
        }

        @Nested
        class AndAuthorIsNotAdmin {
            private final Boolean active = Boolean.TRUE;

            @BeforeEach
            void setUp() {
                User author = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), author);

                post = createPostWithDigitalDocument(author, someString(10), board, stock);
                postId = post.getId();

                updatePostRequest = genRequest(active);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);

                itUtil.assertErrorResponse(response, 400, "게시글 등록자와 현재사용자가 일치하지 않습니다.");
            }
        }
    }

    @Nested
    class WhenAdminUserChangeActiveToInactive {
        private final Boolean active = Boolean.FALSE;

        @BeforeEach
        void setUp() {
            updatePostRequest = genRequest(active);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

            final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                PostDetailsDataResponse.class
            );

            assertDocumentResponse(result, active);
        }

        @Nested
        class WhenIsNotificationTrue {
            private final Boolean isNotification = Boolean.TRUE;

            @BeforeEach
            void setUp() {
                updatePostRequest.setIsNotification(isNotification);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccessWithIsNotificationFalse() throws Exception {
                MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertDocumentResponse(result, active);
            }
        }
    }

    @Nested
    class WhenAdminUserChangeInactiveToActive {
        private final Boolean active = Boolean.TRUE;

        @BeforeEach
        void setUp() {
            post.setStatus(Status.INACTIVE_BY_ADMIN);
            post = itUtil.updatePost(post);

            updatePostRequest = genRequest(active);
            updatePostRequest.setIsNotification(active);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

            final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                PostDetailsDataResponse.class
            );

            assertDocumentResponse(result, active);
        }
    }

    @Nested
    class WhenBoardCategoryIsLeaderElection {
        private final Boolean active = Boolean.TRUE;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
            post.setBoard(board);
            post.setBoardId(board.getId());
            post.setStatus(Status.ACTIVE);
            post.setDigitalDocument(null);
            post = itUtil.updatePost(post);
            postId = post.getId();

            updatePostRequest = genRequest(active);
            updatePostRequest.setDigitalDocument(null);
            updatePostRequest.setIsNotification(active);
        }

        @Test
        void shouldError() throws Exception {
            MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
            itUtil.assertErrorResponse(response, 400, "대표선출 카테고리는 수정할 수 없습니다.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher, UpdatePostRequest request, Long postId) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stockCode, board.getGroup().name(), postId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenGlobalEvent {
        private Push push;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.GLOBALEVENT, BoardGroup.GLOBALEVENT.getCategories().get(0));
            post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
            title = someString(10);
            content = someString(100);

            updatePostRequest = genRequestForGlobalEvent(Boolean.FALSE, Boolean.TRUE);
            updatePostRequest.setPush(new PushRequest()
                .title(title)
                .content(content));
        }

        @Nested
        class WhenError {

            @Nested
            class WhenInActiveToActive {

                @BeforeEach
                void setUp() {
                    postId = post.getId();

                    push = itUtil.createPush(someString(10), PushTargetType.ALL, postId);
                    push.setTargetDatetime(post.getActiveStartDate().plusMinutes(additionalPushTimeMinutes));
                    push.setSendStatus(PushSendStatus.COMPLETE);
                    push.setSendType(PushSendType.SCHEDULE);
                    itUtil.updatePush(push);

                    post.setStatus(Status.INACTIVE_BY_ADMIN);
                    post.setPushId(push.getId());
                    post = itUtil.updatePost(post);

                    updatePostRequest.setIsActive(Boolean.TRUE);
                    updatePostRequest.setPush(new PushRequest()
                        .title(someString(10))
                        .content(someAlphanumericString(100)));
                }

                @Test
                void shouldError() throws Exception {
                    MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
                    itUtil.assertErrorResponse(response, 400, "게시시작일이 이미 지난 미노출 게시글을 다시 노출로 변경할 수 없습니다.");
                }
            }

            @Nested
            class WhenNowAfterActiveStartDate {

                @BeforeEach
                void setUp() {
                    final LocalDateTime now = LocalDateTime.now();
                    post.setStatus(Status.ACTIVE);
                    post.setActiveStartDate(now.plusDays(1));
                    post.setActiveEndDate(now.plusDays(2));
                    post = itUtil.updatePost(post);
                    postId = post.getId();

                    updatePostRequest.setIsActive(Boolean.TRUE);
                    updatePostRequest.setActiveStartDate(now.minusDays(1).toInstant(ZoneOffset.UTC));
                    updatePostRequest.setActiveEndDate(now.plusDays(2).toInstant(ZoneOffset.UTC));
                }

                @Test
                void shouldError() throws Exception {
                    MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
                    itUtil.assertErrorResponse(response, 400, "게시 시작일은 현재일 이후로 수정할 수 없습니다.");
                }
            }

            @Nested
            class WhenActiveEndDateAfterActiveStartDate {

                @BeforeEach
                void setUp() {
                    final LocalDateTime now = LocalDateTime.now();
                    post.setStatus(Status.ACTIVE);
                    post.setActiveStartDate(now.plusDays(1));
                    post.setActiveEndDate(now.plusDays(2));
                    post = itUtil.updatePost(post);
                    postId = post.getId();

                    updatePostRequest.setIsActive(Boolean.TRUE);
                    updatePostRequest.setActiveStartDate(now.plusDays(2).toInstant(ZoneOffset.UTC));
                    updatePostRequest.setActiveEndDate(now.plusDays(1).toInstant(ZoneOffset.UTC));
                }

                @Test
                void shouldError() throws Exception {
                    MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
                    itUtil.assertErrorResponse(response, 400, "게시 종료일을 시작일보다 과거로 수정할 수는 없습니다.");
                }
            }

            @Nested
            class WhenNotMatchGlobalEventCategory {

                @BeforeEach
                void setUp() {
                    board = itUtil.createBoard(stock, BoardGroup.GLOBALBOARD, BoardGroup.GLOBALBOARD.getCategories().get(0));
                    post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
                    postId = post.getId();

                    final Instant now = Instant.now();
                    updatePostRequest.setActiveStartDate(now);
                    updatePostRequest.setActiveEndDate(now.plus(1, ChronoUnit.DAYS));
                }

                @Test
                void shouldError() throws Exception {
                    MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
                    itUtil.assertErrorResponse(response, 400, "게시 시작일이나 종료일은 이벤트/공지 게시글만 수정 가능합니다.");
                }
            }

            @Nested
            class WhenActiveToActiveAndAlreadyStarted {

                @BeforeEach
                void setUp() {
                    final LocalDateTime now = LocalDateTime.now();
                    post.setStatus(Status.ACTIVE);
                    post.setPushId(null);
                    post.setActiveStartDate(now.minusDays(1));
                    post = itUtil.updatePost(post);
                    postId = post.getId();

                    updatePostRequest.setIsActive(Boolean.TRUE);
                }

                @Test
                void shouldError() throws Exception {
                    MvcResult response = callApi(status().isBadRequest(), updatePostRequest, postId);
                    itUtil.assertErrorResponse(response, 400, "게시 시작된 게시글의 푸시는 변경할 수 없습니다.");
                }
            }
        }

        @Nested
        class WhenSuccess {
            private final LocalDateTime now = LocalDateTime.now();

            @BeforeEach
            void setUp() {
                final LocalDateTime activeStartDate = now.plusDays(1);
                final LocalDateTime activeEndDate = now.plusDays(2);
                post.setStatus(Status.ACTIVE);
                post.setActiveStartDate(activeStartDate);
                post.setActiveEndDate(activeEndDate);

                push = itUtil.createPush(someString(10), PushTargetType.ALL, post.getId());
                push.setTargetDatetime(post.getActiveStartDate().plusMinutes(additionalPushTimeMinutes));
                push.setSendStatus(PushSendStatus.READY);
                push.setSendType(PushSendType.SCHEDULE);
                itUtil.updatePush(push);

                post.setPushId(push.getId());
                post = itUtil.updatePost(post);
                postId = post.getId();

                final Notification notification = itUtil.createNotification(postId);
                notification.setActiveStartDate(post.getActiveStartDate());
                notification.setActiveEndDate(post.getActiveEndDate());
                itUtil.updateNotification(notification);

                updatePostRequest.setIsActive(Boolean.TRUE);
                updatePostRequest.setActiveStartDate(activeStartDate.toInstant(ZoneOffset.UTC));
                updatePostRequest.setActiveEndDate(activeEndDate.toInstant(ZoneOffset.UTC));
            }

            @Nested
            class WhenNormal {

                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    final PostDetailsResponse postDetailsResponse = result.getData();
                    assertResponse(postDetailsResponse);
                    assertNotificationResponse(postDetailsResponse);
                    assertPushResponse(postDetailsResponse, updatePostRequest.getIsActive());
                }
            }

            @Nested
            class WhenActiveToInactive {

                @BeforeEach
                void setUp() {
                    post.setStatus(Status.ACTIVE);
                    post.setIsNotification(Boolean.TRUE);
                    post = itUtil.updatePost(post);
                    postId = post.getId();

                    updatePostRequest.setIsActive(Boolean.FALSE);
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    final PostDetailsResponse postDetailsResponse = result.getData();
                    assertNotificationResponse(postDetailsResponse);
                    assertPushResponse(postDetailsResponse, updatePostRequest.getIsActive());
                }
            }

            @Nested
            class WhenInactiveToActiveAndActiveStartDateIsFuture {

                @BeforeEach
                void setUp() {
                    final LocalDateTime activeStartDate = now.plusDays(1);
                    final LocalDateTime activeEndDate = now.plusDays(2);
                    post.setStatus(Status.INACTIVE_BY_ADMIN);
                    post.setIsNotification(Boolean.TRUE);
                    post.setActiveStartDate(activeStartDate);
                    post = itUtil.updatePost(post);
                    postId = post.getId();

                    updatePostRequest.setIsActive(Boolean.TRUE);
                    updatePostRequest.setActiveStartDate(activeStartDate.toInstant(ZoneOffset.UTC));
                    updatePostRequest.setActiveEndDate(activeEndDate.toInstant(ZoneOffset.UTC));
                }

                @Nested
                class WhenHavePush {

                    @BeforeEach
                    void setUp() {
                        push = itUtil.createPush(someString(10), PushTargetType.ALL, post.getId());
                        push.setTargetDatetime(post.getActiveStartDate().plusMinutes(additionalPushTimeMinutes));
                        push.setSendStatus(PushSendStatus.READY);
                        push.setSendType(PushSendType.SCHEDULE);
                        itUtil.updatePush(push);

                        title = push.getTitle();
                        content = push.getContent();

                        post.setPushId(push.getId());
                        post = itUtil.updatePost(post);
                        postId = post.getId();

                        updatePostRequest.setPush(new PushRequest()
                            .title(title)
                            .content(content));
                    }

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        final PostDetailsResponse postDetailsResponse = result.getData();
                        assertNotificationResponse(postDetailsResponse);
                        assertPushResponse(postDetailsResponse, updatePostRequest.getIsActive());
                    }

                }

                @Nested
                class WhenDoseNotHavePush {

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApi(status().isOk(), updatePostRequest, postId);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        final PostDetailsResponse postDetailsResponse = result.getData();
                        assertNotificationResponse(postDetailsResponse);
                        assertPushResponse(postDetailsResponse, updatePostRequest.getIsActive());
                    }
                }
            }

            private void assertResponse(PostDetailsResponse postDetailsResponse) {
                assertThat(postDetailsResponse.getIsPush(), is(Boolean.TRUE));
                assertTime(postDetailsResponse.getActiveStartDate(), updatePostRequest.getActiveStartDate());
                assertTime(postDetailsResponse.getActiveEndDate(), updatePostRequest.getActiveEndDate());
            }

            private void assertNotificationResponse(PostDetailsResponse postDetailsResponse) {
                final Notification databaseNotification = itUtil.findNotificationByPostId(postDetailsResponse.getId()).orElseThrow();

                assertTime(databaseNotification.getActiveStartDate(), updatePostRequest.getActiveStartDate());
                assertTime(databaseNotification.getActiveEndDate(), updatePostRequest.getActiveEndDate());
            }

            private void assertPushResponse(PostDetailsResponse postDetailsResponse, Boolean isActive) {
                final PushDetailsResponse pushDetailsResponse = postDetailsResponse.getPush();

                if (isActive) {
                    final Push databasePush = itUtil.findPush(push.getId()).orElseThrow();
                    assertThat(databasePush.getTitle(), is(title));
                    assertThat(databasePush.getContent(), is(content));
                    assertThat(databasePush.getSendStatus(), is(push.getSendStatus()));
                    assertTime(databasePush.getTargetDatetime(), push.getTargetDatetime());
                } else {
                    final Optional<Push> databasePush = itUtil.findPush(push.getId());
                    assertThat(databasePush.isEmpty(), is(true));
                    assertThat(pushDetailsResponse, nullValue());
                }
            }
        }
    }

    private void assertDocumentResponse(PostDetailsDataResponse result, Boolean active) {
        final PostDetailsResponse createResponse = result.getData();

        assertThat(createResponse.getPoll(), is(nullValue()));
        assertThat(createResponse.getDigitalProxy(), is(nullValue()));
        assertThat(createResponse.getDigitalDocument(), is(notNullValue()));

        assertThat(createResponse.getContent(), is(content));
        assertThat(createResponse.getThumbnailImageUrl(), is(expectedThumbnailImageUrl));
        assertThat(createResponse.getId(), is(notNullValue()));
        assertThat(createResponse.getBoardId(), is(board.getId()));
        assertThat(createResponse.getStatus(), is(
            active ? Status.ACTIVE : Status.INACTIVE_BY_ADMIN
        ));

        ag.act.model.DigitalDocumentResponse digitalDocumentResponse = createResponse.getDigitalDocument();
        assertThat(digitalDocumentResponse, is(notNullValue()));

        assertThat(createResponse.getIsActive(), is(active));
        assertTime(digitalDocumentResponse.getTargetEndDate(), updatePostRequest.getDigitalDocument().getTargetEndDate());
        assertThat(createResponse.getIsNotification(), is(
                active ? updatePostRequest.getIsNotification() : INACTIVE_NOTIFICATION
            )
        );
    }

    private Post createPostWithDigitalDocument(User user, String title, Board board, Stock stock) {
        Post post = itUtil.createPost(board, user.getId(), isAnonymous);
        post.setTitle(someString(10) + title + someString(10));

        User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT, targetStartDate, targetEndDate, referenceDate
        );
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, someAlphanumericString(10), DigitalDocumentAnswerStatus.COMPLETE);

        return itUtil.updatePost(post);
    }
}
