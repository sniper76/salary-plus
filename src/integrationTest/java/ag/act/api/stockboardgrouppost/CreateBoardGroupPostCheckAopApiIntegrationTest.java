package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
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
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("checkstyle:LineLength")
class CreateBoardGroupPostCheckAopApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final int LIMIT_SECONDS = 60;
    private Board board;
    private User user;
    private String jwt;
    private CreatePostRequest request;
    private Stock stock;
    private Solidarity solidarity;
    private LocalDateTime now;
    @MockBean
    private PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;
    @Autowired
    private TimeDisplayFormatter timeDisplayFormatter;

    @BeforeEach
    public void setUp() {
        itUtil.init();

        given(appPreferenceCache.getValue(AppPreferenceType.POST_RESTRICTION_INTERVAL_SECONDS)).willReturn(LIMIT_SECONDS);
        now = LocalDateTime.now();
        given(postAndCommentAopCurrentDateTimeProvider.get()).willReturn(now);

        createStock();
        createBoard();

        request = generateRequest();
    }

    @Nested
    @DisplayName("유저가 지금으로부터 쿨다운 시간 이내에 게시글을 작성한 경우")
    class WhenUserHasPostedWithinLimitedTime {
        private LocalDateTime postCreationTime;

        @BeforeEach
        void setUp() {
            postCreationTime = now.minusSeconds(someIntegerBetween(0, LIMIT_SECONDS - 1));
        }

        @Nested
        @DisplayName("어드민 유저가 아닌 일반 유저이고")
        class AndUserIsNotAdmin {
            private long remainingSeconds;
            private String remainingTimeDisplay;

            @BeforeEach
            void setUp() {
                createNotAdminUser();
                createPostAtTime(postCreationTime);

                remainingSeconds = Duration.between(now, postCreationTime.plusSeconds(LIMIT_SECONDS)).getSeconds();
                remainingTimeDisplay = timeDisplayFormatter.format(remainingSeconds);
            }

            @Nested
            @DisplayName("현재가 게시글 등록 쿨다운 시간인 경우")
            class AndPostingCooldownIsActive {
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
                        "잠시 후 다시 시도해 주세요. 게시글 등록은 도배 방지를 위해 일정 시간 간격을 두고 가능합니다.(%s 남음)".formatted(remainingTimeDisplay)
                    );
                }
            }

            @Nested
            @DisplayName("현재가 게시글 등록 쿨다운 시간이 아닌 경우")
            class AndPostingCooldownIsExpired {
                @BeforeEach
                void setUp() {
                    given(postAndCommentAopCurrentDateTimeProvider.getSecondsUntil(any(LocalDateTime.class)))
                        .willReturn(0L);
                }

                @Test
                @DisplayName("게시글을 작성한다.")
                void shouldReturnSuccess() throws Exception {
                    PostDetailsDataResponse result = callApiAndGetResult(status().isOk());

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
                createPostAtTime(postCreationTime);
            }

            @Test
            @DisplayName("게시글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                PostDetailsDataResponse result = callApiAndGetResult(status().isOk());

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
    @DisplayName("유저가 지금으로부터 쿨다운 시간 이내에 게시글을 작성하지 않은 경우")
    class WhenUserHasNotPostedWithinLimitedTime {
        @BeforeEach
        void setUp() {
            createNotAdminUser();
        }

        @Nested
        @DisplayName("유저가 게시글을 작성한 적이 없는 경우")
        class AndUserHasNeverPosted {
            @Test
            @DisplayName("게시글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                PostDetailsDataResponse result = callApiAndGetResult(status().isOk());

                assertResponse(result.getData());
            }
        }

        @Nested
        @DisplayName("유저가 아주 오래 전에 게시글을 작성한 경우")
        class AndUserHasPostedMoreThanLimitedTimeAgo {

            @BeforeEach
            void setUp() {
                LocalDateTime postCreationTime = now.minusSeconds(someIntegerBetween(1, LIMIT_SECONDS) + LIMIT_SECONDS);
                createPostAtTime(postCreationTime);
            }

            @Test
            @DisplayName("게시글을 작성한다.")
            void shouldReturnSuccess() throws Exception {
                PostDetailsDataResponse result = callApiAndGetResult(status().isOk());

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

    private void createPostAtTime(LocalDateTime createdAt) {
        Post post = itUtil.createPost(board, user.getId());
        post.setCreatedAt(createdAt);
        itUtil.updatePost(post);
    }

    private void assertResponse(PostDetailsResponse response) {
        assertThat(response.getId(), is(notNullValue()));
        assertThat(response.getTitle(), is(request.getTitle()));
        assertThat(response.getContent(), is(request.getContent()));
        assertThat(response.getBoardId(), is(board.getId()));
        assertThat(response.getPostImageList(), is(empty()));
        assertThat(response.getStatus(), is(Status.ACTIVE));

        final UserProfileResponse userProfile = response.getUserProfile();
        assertThat(userProfile.getNickname(), is(user.getNickname()));
    }

    private CreatePostRequest generateRequest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setContent(someAlphanumericString(20));
        request.setTitle(someString(10));
        request.isAnonymous(Boolean.FALSE);
        return request;
    }

    private PostDetailsDataResponse callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        return objectMapperUtil.toResponse(callApi(resultMatcher).getResponse().getContentAsString(), PostDetailsDataResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup().name())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}