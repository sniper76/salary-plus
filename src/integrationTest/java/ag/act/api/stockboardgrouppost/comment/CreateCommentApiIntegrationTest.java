package ag.act.api.stockboardgrouppost.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil.TestHtmlContent;
import ag.act.constants.MessageConstants;
import ag.act.core.holder.web.WebBrowserDetector;
import ag.act.core.holder.web.WebBrowserDetectorFactory;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserBadgeVisibility;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.ClientType;
import ag.act.enums.UserBadgeType;
import ag.act.exception.NotFoundException;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import static ag.act.TestUtil.someHtmlContent;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static ag.act.itutil.authentication.AuthenticationTestUtil.xAppVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class CreateCommentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";

    private ag.act.model.CreateCommentRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private UserHoldingStock userHoldingStock;
    private Solidarity solidarity;
    private TestHtmlContent testHtmlContent;
    private String appVersion;

    @MockBean
    private WebBrowserDetectorFactory webBrowserDetectorFactory;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
        solidarity = itUtil.createSolidarity(stock.getCode());
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);

        setUpAppVersion();
    }

    private void setUpAppVersion() {
        appVersion = someThing(AppPreferenceType.MIN_APP_VERSION.getDefaultValue(), X_APP_VERSION_WEB);

        WebBrowserDetector webBrowserDetector = mock(WebBrowserDetector.class);
        given(webBrowserDetectorFactory.createWebBrowserDetector(any(HttpServletRequest.class))).willReturn(webBrowserDetector);
        given(webBrowserDetector.isRequestFromWebBrowser()).willReturn(appVersion.equals(X_APP_VERSION_WEB));
    }

    private ag.act.model.CreateCommentRequest genAnonymousRequest() {
        return genRequest(Boolean.TRUE);
    }

    private CreateCommentRequest genRequest(Boolean isAnonymous) {
        CreateCommentRequest request = new CreateCommentRequest();

        testHtmlContent = someHtmlContent();
        request.setContent(testHtmlContent.html());
        request.setIsAnonymous(isAnonymous);

        return request;
    }

    @NotNull
    private MvcResult callApiWithJwt(ag.act.model.CreateCommentRequest request, String jwt) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt), xAppVersion(appVersion)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertAnonymousFromDatabase(User user, int anonymousCount) {
        final List<Comment> comments = itUtil.findComments(post.getId(), user.getId());
        comments.forEach(comment -> {
                assertThat(comment.getCommentUserProfile().getNickname(), is(MessageConstants.ANONYMOUS_NAME));
                if (Objects.equals(post.getUserId(), comment.getUserId())) {
                    assertThat(comment.getAnonymousCount(), is(0));
                } else {
                    assertThat(comment.getAnonymousCount(), is(anonymousCount));
                }
            }
        );
    }

    private void assertFromDatabase(Long commentId) {
        Comment comment = itUtil.findCommentById(commentId)
            .orElseThrow(() -> new NotFoundException("[TEST] 댓글 정보를 찾을 수 없습니다."));

        if (appVersion.equals(X_APP_VERSION_WEB)) {
            assertThat(comment.getClientType(), is(ClientType.WEB));
        } else {
            assertThat(comment.getClientType(), is(ClientType.APP));
        }
    }

    @Nested
    class WhenCreatePostComment {
        private Boolean isSolidarityLeader;

        @Nested
        class AndNormal {

            @Nested
            class ByNotSolidarityLeader {
                @BeforeEach
                void setUp() {
                    request = genRequest(someBoolean());
                    isSolidarityLeader = false;
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final MvcResult response = callApiWithJwt(request, jwt);
                    assertResponse(getResponse(response));
                }
            }

            @Nested
            class BySolidarityLeader {
                @BeforeEach
                void setUp() {
                    request = genRequest(false);
                    itUtil.createSolidarityLeader(solidarity, user.getId());
                    isSolidarityLeader = true;
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final MvcResult response = callApiWithJwt(request, jwt);
                    assertResponse(getResponse(response));
                }
            }

            @Nested
            class WithHtmlContent {

                @BeforeEach
                void setUp() {
                    request = genRequest(false);
                    itUtil.createSolidarityLeader(solidarity, user.getId());
                    isSolidarityLeader = true;
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final MvcResult response = callApiWithJwt(request, jwt);
                    final CommentDataResponse commentDataResponse = getResponse(response);
                    assertResponse(commentDataResponse);
                }
            }

            private void assertResponse(CommentDataResponse result) {
                final CommentResponse commentResponse = result.getData();

                assertThat(commentResponse.getId(), is(notNullValue()));
                assertCommentContent(commentResponse);
                assertThat(commentResponse.getLikeCount(), is(0L));
                assertThat(commentResponse.getReplyCommentCount(), is(0L));
                assertThat(commentResponse.getLiked(), is(false));
                assertThat(commentResponse.getDeleted(), is(false));

                if (request.getIsAnonymous()) {
                    assertThat(commentResponse.getUserProfile().getNickname(), is(containsString("익명")));
                } else {
                    assertThat(commentResponse.getUserProfile().getNickname(), is(user.getNickname()));
                    assertThat(commentResponse.getUserProfile().getProfileImageUrl(), is(user.getProfileImageUrl()));
                    assertThat(commentResponse.getUserProfile().getIndividualStockCountLabel(), is("1주+"));
                    assertThat(commentResponse.getUserProfile().getTotalAssetLabel(), is(nullValue()));
                    assertThat(commentResponse.getUserProfile().getIsSolidarityLeader(), is(isSolidarityLeader));
                }
                assertThat(commentResponse.getUserProfile().getUserIp(), is("127.0"));

                assertFromDatabase(commentResponse.getId());
            }
        }

        @Nested
        class AndUserBadgeVisibility {
            private final Integer closingPrice = 50000;
            private final Long stockQuantity = 50000L;

            @BeforeEach
            void setUp() {
                request = genRequest(Boolean.FALSE);
                itUtil.createUserBadgeVisibility(user.getId());
                userHoldingStock.setQuantity(stockQuantity);
                itUtil.updateUserHoldingStock(userHoldingStock);
                stock.setClosingPrice(closingPrice);
                itUtil.updateStock(stock);
            }

            private void assertResponse(ag.act.model.CommentDataResponse result) {
                final ag.act.model.CommentResponse createUpdateResponse = result.getData();

                assertThat(createUpdateResponse.getId(), is(notNullValue()));
                assertCommentContent(createUpdateResponse);
                assertThat(createUpdateResponse.getLikeCount(), is(0L));
                assertThat(createUpdateResponse.getReplyCommentCount(), is(0L));
                assertThat(createUpdateResponse.getLiked(), is(false));
                assertThat(createUpdateResponse.getDeleted(), is(false));

                assertThat(createUpdateResponse.getUserProfile().getNickname(), is(user.getNickname()));
                assertThat(createUpdateResponse.getUserProfile().getProfileImageUrl(), is(user.getProfileImageUrl()));
                assertThat(createUpdateResponse.getUserProfile().getUserIp(), is("127.0"));
            }

            @NotNull
            private UserBadgeVisibility getUserBadgeVisibility(List<UserBadgeVisibility> userBadgeVisibilities, UserBadgeType type) {
                return userBadgeVisibilities
                    .stream()
                    .filter(it -> it.getType() == type)
                    .findFirst().orElseThrow();
            }

            @Nested
            class AndNotVisibleTotalAsset {

                @BeforeEach
                void setUp() {
                    final List<UserBadgeVisibility> userBadgeVisibilities = itUtil.findAllUserBadgeVisibilityByUserId(user.getId());
                    final UserBadgeVisibility userBadgeVisibilityTotalAsset = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.TOTAL_ASSET);

                    userBadgeVisibilityTotalAsset.setIsVisible(Boolean.FALSE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityTotalAsset);

                    final UserBadgeVisibility userBadgeVisibilityStockQuantity = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.STOCK_QUANTITY);

                    userBadgeVisibilityStockQuantity.setIsVisible(Boolean.TRUE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityStockQuantity);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(request, jwt);

                    final CommentDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        CommentDataResponse.class
                    );

                    assertResponse(result);
                    final CommentResponse commentResponse = result.getData();
                    assertThat(commentResponse.getUserProfile().getIndividualStockCountLabel(), is("5만주+"));
                    assertThat(commentResponse.getUserProfile().getTotalAssetLabel(), is(nullValue()));
                }
            }

            @Nested
            class AndNotVisibleIndividualStockStockQuantity {

                @BeforeEach
                void setUp() {
                    final List<UserBadgeVisibility> userBadgeVisibilities = itUtil.findAllUserBadgeVisibilityByUserId(user.getId());
                    final UserBadgeVisibility userBadgeVisibilityTotalAsset = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.TOTAL_ASSET);

                    userBadgeVisibilityTotalAsset.setIsVisible(Boolean.TRUE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityTotalAsset);

                    final UserBadgeVisibility userBadgeVisibilityStockQuantity = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.STOCK_QUANTITY);

                    userBadgeVisibilityStockQuantity.setIsVisible(Boolean.FALSE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityStockQuantity);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(request, jwt);

                    final ag.act.model.CommentDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        ag.act.model.CommentDataResponse.class
                    );

                    assertResponse(result);
                    final ag.act.model.CommentResponse createUpdateResponse = result.getData();
                    assertThat(createUpdateResponse.getUserProfile().getIndividualStockCountLabel(), is(nullValue()));
                    assertThat(createUpdateResponse.getUserProfile().getTotalAssetLabel(), is("10억+"));
                }
            }
        }
    }

    private void assertCommentContent(CommentResponse createUpdateResponse) {
        assertThat(createUpdateResponse.getContent(), is(testHtmlContent.expectedHtml()));
    }

    private CommentDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentDataResponse.class
        );
    }

    @Nested
    class WhenSameUserCreatePostCommentsWithAnonymousMode {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock);
            post = itUtil.createPost(board, user.getId());
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);
        }

        @DisplayName("Should always return the same 익명 when same user makes several comments")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genRequest(true)));
            assertResponse(callApi(genRequest(true)));
            assertResponse(callApi(genRequest(true)));

            assertAnonymousFromDatabase(user, 1);
        }

        private CommentDataResponse callApi(CreateCommentRequest request) throws Exception {
            final MvcResult response = callApiWithJwt(request, jwt);

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                CommentDataResponse.class
            );
        }

        private void assertResponse(CommentDataResponse result) {
            final CommentResponse createUpdateResponse = result.getData();
            assertThat(createUpdateResponse.getUserProfile().getNickname(), is(MessageConstants.ANONYMOUS_NAME));
        }
    }

    @Nested
    class WhenDifferentUsersCreatePostCommentsWithAnonymousMode {

        private User anonymousUser1;
        private User anonymousUser2;
        private User anonymousUser3;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock);
            itUtil.createSolidarity(stock.getCode());
            post = itUtil.createPost(board, user.getId());

            anonymousUser1 = itUtil.createUser();
            anonymousUser2 = itUtil.createUser();
            anonymousUser3 = itUtil.createUser();

            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser1);
            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser2);
            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser3);
        }

        @DisplayName("Should return 익명1, 익명2, 익명3 when different users make comments")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genRequest(true), anonymousUser1), "익명1");
            assertResponse(callApi(genRequest(true), anonymousUser2), "익명2");
            assertResponse(callApi(genRequest(true), anonymousUser1), "익명1");
            assertResponse(callApi(genRequest(true), anonymousUser2), "익명2");
            assertResponse(callApi(genRequest(true), anonymousUser3), "익명3");
            assertResponse(callApi(genRequest(true), anonymousUser3), "익명3");

            assertAnonymousFromDatabase(anonymousUser1, 1);
            assertAnonymousFromDatabase(anonymousUser2, 2);
            assertAnonymousFromDatabase(anonymousUser3, 3);
        }

        private CommentDataResponse callApi(CreateCommentRequest request, User loginUser) throws Exception {

            final String jwt = itUtil.createJwt(loginUser.getId());

            final MvcResult response = callApiWithJwt(request, jwt);

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                CommentDataResponse.class
            );
        }
    }

    @Nested
    class WhenSamePostWriterCreatePostCommentsWithAnonymousMode {

        private User anonymousUser1;
        private User anonymousUser2;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock);
            itUtil.createSolidarity(stock.getCode());
            post = itUtil.createPost(board, user.getId(), true);

            anonymousUser1 = itUtil.createUser();
            anonymousUser2 = itUtil.createUser();

            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser1);//익명1
            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser2);//익명2
            itUtil.createUserHoldingStock(stock.getCode(), user);
        }

        @DisplayName("Should return 익명1, 익명2, 익명 when different users make comments and match post writer")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genRequest(true), anonymousUser1), "익명1");
            assertResponse(callApi(genRequest(true), anonymousUser2), "익명2");
            assertResponse(callApi(genRequest(true), user), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), anonymousUser1), "익명1");
            assertResponse(callApi(genRequest(true), anonymousUser2), "익명2");
            assertResponse(callApi(genRequest(true), user), MessageConstants.ANONYMOUS_NAME);

            assertAnonymousFromDatabase(anonymousUser1, 1);
            assertAnonymousFromDatabase(anonymousUser2, 2);
            assertAnonymousFromDatabase(user, 0);
        }

        private ag.act.model.CommentDataResponse callApi(ag.act.model.CreateCommentRequest request, User loginUser) throws Exception {

            final String jwt = itUtil.createJwt(loginUser.getId());

            final MvcResult response = callApiWithJwt(request, jwt);

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.CommentDataResponse.class
            );
        }
    }

    @Nested
    class WhenPostDeleted {

        private User loginUser;

        @BeforeEach
        void setUp() {
            post.setStatus(Status.DELETED_BY_USER);
            itUtil.updatePost(post);

            loginUser = itUtil.createUser();
            itUtil.createUserHoldingStock(stock.getCode(), loginUser);
        }

        @DisplayName("Should when create comment on deleted post")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genRequest(false), loginUser), loginUser.getNickname());
        }

        private CommentDataResponse callApi(ag.act.model.CreateCommentRequest request, User loginUser) throws Exception {

            final String jwt = itUtil.createJwt(loginUser.getId());

            final MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt), xAppVersion(appVersion)))
                )
                .andExpect(status().isOk())
                .andReturn();

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                CommentDataResponse.class
            );
        }

    }

    @Nested
    class WhenAnonymousUserWritesMoreComments {

        private User anonymousUser1;
        private User adminUser;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock);
            itUtil.createSolidarity(stock.getCode());
            post = itUtil.createPost(board, user.getId(), true);

            anonymousUser1 = itUtil.createUser();
            adminUser = itUtil.createAdminUser();

            itUtil.createUserHoldingStock(stock.getCode(), anonymousUser1);//익명1
            itUtil.createUserHoldingStock(stock.getCode(), user);
        }

        @DisplayName("한번 익명 닉네임이 주어지면, 그 이후에 댓글을 쓸때, 계속 동일한 익명 님네임으로 세팅이 되어야 한다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genAnonymousRequest(), anonymousUser1), "익명1");
            assertResponse(callApi(genAnonymousRequest(), adminUser), "익명2");
            assertResponse(callApi(genAnonymousRequest(), user), "익명");
            assertResponse(callApi(genAnonymousRequest(), anonymousUser1), "익명1");
            assertResponse(callApi(genAnonymousRequest(), adminUser), "익명2");
            assertResponse(callApi(genAnonymousRequest(), user), "익명");
            assertResponse(callApi(genAnonymousRequest(), adminUser), "익명2");
            assertResponse(callApi(genAnonymousRequest(), adminUser), "익명2");
        }

        private ag.act.model.CommentDataResponse callApi(ag.act.model.CreateCommentRequest request, User loginUser) throws Exception {

            final String jwt = itUtil.createJwt(loginUser.getId());

            final MvcResult response = callApiWithJwt(request, jwt);

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.CommentDataResponse.class
            );
        }
    }

    private void assertResponse(CommentDataResponse result, String nickname) {
        final CommentResponse commentResponse = result.getData();
        assertThat(commentResponse.getUserProfile().getNickname(), is(nickname));
        assertFromDatabase(commentResponse.getId());
    }
}
