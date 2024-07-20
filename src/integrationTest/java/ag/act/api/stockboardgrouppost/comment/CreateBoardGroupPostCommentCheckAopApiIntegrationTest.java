package ag.act.api.stockboardgrouppost.comment;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.CommentType;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import ag.act.module.post.PostAndCommentAopCurrentDateTimeProvider;
import ag.act.module.time.TimeDisplayFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Duration;
import java.time.LocalDateTime;

import static ag.act.TestUtil.someNonAdminUserWritableBoardGroupCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class CreateBoardGroupPostCommentCheckAopApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";
    private static final int LIMIT_SECONDS = 5;

    private Board board;
    private User user;
    private String jwt;
    private CreateCommentRequest request;
    private Stock stock;
    private Solidarity solidarity;
    private Post post;
    private LocalDateTime now;
    @MockBean
    PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;
    @Autowired
    private TimeDisplayFormatter timeDisplayFormatter;

    @BeforeEach
    public void setUp() {
        itUtil.init();
        given(appPreferenceCache.getValue(AppPreferenceType.COMMENT_RESTRICTION_INTERVAL_SECONDS)).willReturn(LIMIT_SECONDS);

        now = LocalDateTime.now();
        given(postAndCommentAopCurrentDateTimeProvider.get()).willReturn(now);

        createStock();
        createBoard();
        createPost();

        request = generateRequest();
    }

    @Nested
    @DisplayName("유저가 지금으로부터 쿨다운 시간 이내에 댓글을 작성한 경우")
    class WhenUserHasCommentedWithinLimitedTime {
        private LocalDateTime commentCreationTime;

        @BeforeEach
        void setUp() {
            commentCreationTime = now.minusSeconds(someIntegerBetween(0, LIMIT_SECONDS - 1));
        }

        @Nested
        @DisplayName("어드민 유저가 아닌 일반 유저이고")
        class AndUserIsNotAdmin {
            private long remainingSeconds;
            private String remainingTimeDisplay;

            @BeforeEach
            void setUp() {
                createNotAdminUser();
                createCommentAtTime(commentCreationTime);

                remainingSeconds = Duration.between(now, commentCreationTime.plusSeconds(LIMIT_SECONDS)).getSeconds();
                remainingTimeDisplay = timeDisplayFormatter.format(remainingSeconds);
            }

            @Nested
            @DisplayName("현재가 댓글 등록 쿨다운 시간인 경우")
            class AndCommentingCooldownIsActive {
                @BeforeEach
                void setUp() {
                    given(postAndCommentAopCurrentDateTimeProvider.getSecondsUntil(any(LocalDateTime.class))).willReturn(remainingSeconds);
                }

                @Test
                @DisplayName("에러 및 도배 방지 문구 및 재작성 가능 시각까지 남은 시간을 반환한다.")
                void shouldReturnBadRequest() throws Exception {
                    MvcResult response = callApi(status().isTooManyRequests());

                    itUtil.assertErrorResponse(
                        response,
                        429,
                        "잠시 후 다시 시도해 주세요. 댓글과 답글 등록은 도배 방지를 위해 일정 시간 간격을 두고 가능합니다.(%s 남음)".formatted(remainingTimeDisplay)
                    );
                }
            }

            @Nested
            @DisplayName("현재가 댓글 등록 쿨다운 시간이 아닌 경우")
            class AndCommentingCooldownIsExpired {
                @BeforeEach
                void setUp() {
                    given(postAndCommentAopCurrentDateTimeProvider.getSecondsUntil(commentCreationTime.plusSeconds(LIMIT_SECONDS)))
                        .willReturn(0L);
                }

                @Test
                @DisplayName("댓글을 작성한다.")
                void shouldReturnSuccess() throws Exception {
                    CommentDataResponse result = callApiAndGetResult(status().isOk());

                    assertResponse(result.getData());
                }
            }
        }

        @Nested
        @DisplayName("어드민 유저인 경우")
        class AndUserIsAdmin {
            @BeforeEach
            void setUp() {
                createAdminUser();
                createCommentAtTime(commentCreationTime);
            }

            @Test
            @DisplayName("댓글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                CommentDataResponse result = callApiAndGetResult(status().isOk());

                assertResponse(result.getData());
            }

            private void createAdminUser() {
                user = itUtil.createAdminUser();
                jwt = itUtil.createJwt(user.getId());
                itUtil.createUserHoldingStock(stock.getCode(), user);
            }
        }
    }

    @Nested
    @DisplayName("유저가 지금으로부터 쿨다운 시간 이내에 댓글을 작성하지 않은 경우")
    class WhenUserHasNotCommentedWithinLimitedTime {
        @BeforeEach
        void setUp() {
            createNotAdminUser();
        }

        @Nested
        @DisplayName("유저가 댓글을 작성한 적이 없는 경우")
        class AndUserHasNeverCommented {

            @Test
            @DisplayName("댓글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                CommentDataResponse result = callApiAndGetResult(status().isOk());

                assertResponse(result.getData());
            }
        }

        @Nested
        @DisplayName("유저가 아주 오래 전에 게시글을 작성한 경우")
        class AndUserHasCommentedMoreThanLimitedTimeAgo {

            @BeforeEach
            void setUp() {
                LocalDateTime commentCreationTime = now.minusSeconds(someIntegerBetween(1, LIMIT_SECONDS) + LIMIT_SECONDS);
                createCommentAtTime(commentCreationTime);
            }

            @Test
            @DisplayName("댓글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                CommentDataResponse result = callApiAndGetResult(status().isOk());

                assertResponse(result.getData());
            }
        }
    }

    private void createStock() {
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
    }

    private void createBoard() {
        TestUtil.BoardGroupCategory boardGroupCategory = someNonAdminUserWritableBoardGroupCategory();
        board = itUtil.createBoard(stock, boardGroupCategory.boardGroup(), boardGroupCategory.boardCategory());
    }

    private void createNotAdminUser() {
        user = itUtil.createAcceptorUser();
        itUtil.createStockAcceptorUser(stock.getCode(), user);
        itUtil.createSolidarityLeader(solidarity, user.getId());
        jwt = itUtil.createJwt(user.getId());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void createPost() {
        post = itUtil.createPost(board, itUtil.createUser().getId());
    }

    private void createCommentAtTime(LocalDateTime createdAt) {
        Comment comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
        comment.setCreatedAt(createdAt);
        itUtil.updateComment(comment);
    }

    private void assertResponse(CommentResponse response) {
        assertThat(response.getId(), is(notNullValue()));
        assertThat(response.getContent(), is(request.getContent()));
        assertThat(response.getStatus(), is(Status.ACTIVE.name()));
        assertThat(response.getReplyCommentCount(), is(0L));

        final UserProfileResponse userProfile = response.getUserProfile();
        assertThat(userProfile.getNickname(), is(user.getNickname()));
    }

    private CreateCommentRequest generateRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(someAlphanumericString(100));
        request.setIsAnonymous(false);
        return request;
    }

    private CommentDataResponse callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        return objectMapperUtil.toResponse(callApi(resultMatcher).getResponse().getContentAsString(), CommentDataResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup().name(), post.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
